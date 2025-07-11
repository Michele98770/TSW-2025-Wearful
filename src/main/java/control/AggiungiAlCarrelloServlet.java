package control;

import model.carrello.CarrelloBean;
import model.carrello.CarrelloDAO;
import model.cartitem.CartItemBean;
import model.cartitem.CartItemDAO;
import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;
import model.utente.UtenteBean;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Type;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@WebServlet("/AggiungiAlCarrelloServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class AggiungiAlCarrelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CarrelloDAO carrelloDAO;
    private CartItemDAO cartItemDAO;
    private ProdottoDAO prodottoDAO;
    private Gson gson;

    private static final String UPLOAD_DIRECTORY = "img/custom_images";

    public void init() throws ServletException {
        super.init();
        carrelloDAO = new CarrelloDAO();
        cartItemDAO = new CartItemDAO();
        prodottoDAO = new ProdottoDAO();
        gson = new Gson();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean loggedUser = (UtenteBean) session.getAttribute("user");

        Long idProdotto = null;
        int quantita = 0;
        CartItemBean itemToDisplay = null;

        try {
            idProdotto = Long.parseLong(request.getParameter("idProdotto"));
            quantita = Integer.parseInt(request.getParameter("quantita"));
            if (quantita <= 0) {
                request.setAttribute("errorMessage", "La quantità deve essere almeno 1.");
                request.getRequestDispatcher("/prodotto.jsp?id=" + idProdotto).forward(request, response);
                return;
            }

            ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(idProdotto);
            if (prodotto == null) {
                request.setAttribute("errorMessage", "Prodotto non trovato.");
                request.getRequestDispatcher("/prodotto.jsp?id=" + idProdotto).forward(request, response);
                return;
            }

            boolean finalIsPersonalized = false;
            String finalImgPath = prodotto.getImgPath();

            if (prodotto.isPersonalizzabile()) {
                Part filePart = request.getPart("customImageFile");
                String fileName = null;
                long fileSize = 0;

                if (filePart != null) {
                    fileName = getFileName(filePart);
                    fileSize = filePart.getSize();
                }

                if (fileName != null && !fileName.isEmpty() && fileSize > 0) {

                    String applicationPath = getServletContext().getRealPath("");
                    String uploadPath = applicationPath + File.separator + UPLOAD_DIRECTORY;

                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        boolean created = uploadDir.mkdirs();
                        if (!created) {
                            System.err.println("ERRORE: Impossibile creare la directory di upload: " + uploadPath);
                            request.setAttribute("errorMessage", "Errore del server: impossibile salvare l'immagine personalizzata (problema directory).");
                            request.getRequestDispatcher("/prodotto.jsp?id=" + idProdotto).forward(request, response);
                            return;
                        }
                    }

                    String fileExtension = "";
                    int dotIndex = fileName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        fileExtension = fileName.substring(dotIndex);
                    }
                    String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                    Path filePath = Paths.get(uploadPath, uniqueFileName);

                    try (InputStream input = filePart.getInputStream()) {
                        Files.copy(input, filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        finalIsPersonalized = true;
                        finalImgPath = "./" + UPLOAD_DIRECTORY + "/" + uniqueFileName;
                    } catch (IOException e) {
                        System.err.println("ERRORE: Errore IO durante il salvataggio del file: " + e.getMessage());
                        e.printStackTrace();
                        request.setAttribute("errorMessage", "Errore del server: impossibile salvare l'immagine personalizzata (problema scrittura file).");
                        request.getRequestDispatcher("/prodotto.jsp?id=" + idProdotto).forward(request, response);
                        return;
                    }

                } else {
                    finalIsPersonalized = false;
                    finalImgPath = prodotto.getImgPath();
                }
            } else {
                finalIsPersonalized = false;
                finalImgPath = prodotto.getImgPath();
            }

            if (loggedUser != null) {
                CarrelloBean carrelloUtente = carrelloDAO.doRetrieveByUtente(loggedUser.getEmail());
                if (carrelloUtente == null) {
                    carrelloUtente = new CarrelloBean();
                    carrelloUtente.setIdUtente(loggedUser.getEmail());
                    carrelloDAO.doSave(carrelloUtente);
                    session.setAttribute("carrello", carrelloUtente);
                }

                CartItemBean existingItem = cartItemDAO.doRetrieveByProdottoAndCarrello(idProdotto, carrelloUtente.getId());

                if (existingItem != null) {
                    existingItem.setQuantita(existingItem.getQuantita() + quantita);
                    existingItem.setPersonalizzato(finalIsPersonalized);
                    existingItem.setImgPath(finalImgPath);
                    cartItemDAO.doUpdate(existingItem);
                    itemToDisplay = existingItem;
                } else {
                    CartItemBean newItem = new CartItemBean(
                            idProdotto,
                            carrelloUtente.getId(),
                            quantita,
                            finalIsPersonalized,
                            finalImgPath
                    );
                    cartItemDAO.doSave(newItem);
                    itemToDisplay = newItem;
                }
                session.setAttribute("cartCount", CarrelloServlet.getCartItemCount(request, loggedUser.getEmail()));
            } else {
                if (finalIsPersonalized) {
                    request.setAttribute("errorMessage", "Per aggiungere prodotti personalizzati con un'immagine, devi essere loggato.");
                    request.getRequestDispatcher("/prodotto.jsp?id=" + idProdotto).forward(request, response);
                    return;
                }

                Map<Long, Integer> guestCart = getGuestCartFromCookies(request);

                int currentGuestQuantity = guestCart.getOrDefault(idProdotto, 0) + quantita;
                guestCart.put(idProdotto, currentGuestQuantity);
                saveGuestCartToCookies(response, guestCart);

                itemToDisplay = new CartItemBean();
                itemToDisplay.setProductID(idProdotto);
                itemToDisplay.setQuantita(currentGuestQuantity);
                itemToDisplay.setPersonalizzato(false);
                itemToDisplay.setImgPath(prodotto.getImgPath());

                session.setAttribute("cartCount", CarrelloServlet.getCartItemCount(request, null));
            }

            request.setAttribute("prodotto", prodotto);
            request.setAttribute("item", itemToDisplay);
            RequestDispatcher rd = request.getRequestDispatcher("./aggiuntaCarrello.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Si è verificato un errore inaspettato: " + e.getMessage());
            request.getRequestDispatcher("/prodotto.jsp?id=" + (idProdotto != null ? idProdotto : "")).forward(request, response);
        }
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return null;
    }

    private Map<Long, Integer> getGuestCartFromCookies(HttpServletRequest request) {
        Map<Long, Integer> guestCart = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("guest_cart".equals(cookie.getName())) {
                    try {
                        String cartJsonString = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                        Type type = new TypeToken<Map<Long, Integer>>() {}.getType();
                        guestCart = gson.fromJson(cartJsonString, type);
                        if (guestCart == null) {
                            guestCart = new HashMap<>();
                        }
                    } catch (Exception ignored) {
                        System.err.println("ERRORE: Impossibile decodificare il cookie del carrello ospite. Creazione di un nuovo carrello vuoto.");
                    }
                    break;
                }
            }
        }
        return guestCart;
    }

    private void saveGuestCartToCookies(HttpServletResponse response, Map<Long, Integer> guestCart) {
        String cartJsonString = gson.toJson(guestCart);

        try {
            Cookie cookie = new Cookie("guest_cart", URLEncoder.encode(cartJsonString, StandardCharsets.UTF_8));
            cookie.setMaxAge(7 * 24 * 60 * 60);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            System.err.println("ERRORE: Errore durante la creazione o il salvataggio del cookie del carrello ospite: " + e.getMessage());
            e.printStackTrace();
        }
    }
}