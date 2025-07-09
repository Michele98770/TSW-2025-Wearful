package control;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.carrello.CarrelloBean;
import model.carrello.CarrelloDAO;
import model.cartitem.CartItemBean;
import model.cartitem.CartItemDAO;
import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;
import model.utente.UtenteBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/CarrelloServlet")
public class CarrelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CarrelloDAO carrelloDAO;
    private CartItemDAO cartItemDAO;
    private ProdottoDAO prodottoDAO;
    private Gson gson;

    public void init() throws ServletException {
        super.init();
        carrelloDAO = new CarrelloDAO();
        cartItemDAO = new CartItemDAO();
        prodottoDAO = new ProdottoDAO();
        gson = new Gson();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean loggedUser = (UtenteBean) session.getAttribute("user");

        CarrelloBean carrelloUtente = null;
        Map<CartItemBean, ProdottoBean> cartItemsWithProducts = new LinkedHashMap<>();
        int cartCount = 0;

        try {
            if (loggedUser != null) {
                carrelloUtente = carrelloDAO.doRetrieveByUtente(loggedUser.getEmail());
                if (carrelloUtente == null) {
                    carrelloUtente = new CarrelloBean();
                    carrelloUtente.setIdUtente(loggedUser.getEmail());
                    carrelloDAO.doSave(carrelloUtente);
                    carrelloUtente = carrelloDAO.doRetrieveByUtente(loggedUser.getEmail());
                }

                List<CartItemBean> rawCartItems = cartItemDAO.doRetrieveByCarrello(carrelloUtente.getId());
                for (CartItemBean item : rawCartItems) {
                    ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(item.getProductID());
                    if (prodotto != null) {
                        cartItemsWithProducts.put(item, prodotto);
                    }
                }
                cartCount = rawCartItems.size(); // Conta gli elementi distinti nel carrello dell'utente

            } else {
                Map<Long, Integer> guestCartData = getGuestCartFromCookies(request);
                for (Map.Entry<Long, Integer> entry : guestCartData.entrySet()) {
                    Long idProdotto = entry.getKey();
                    int quantita = entry.getValue();

                    ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(idProdotto);
                    if (prodotto != null) {
                        CartItemBean virtualItem = new CartItemBean(null, idProdotto, null, quantita, prodotto.isPersonalizzabile(), prodotto.getImgPath());
                        cartItemsWithProducts.put(virtualItem, prodotto);
                    }
                }
                cartCount = guestCartData.size(); // Conta gli elementi distinti nel carrello guest
            }

            session.setAttribute("carrello", carrelloUtente);
            request.setAttribute("cartItemsWithProducts", cartItemsWithProducts);
            session.setAttribute("cartCount", cartCount); // Imposta il contatore in sessione

            String message = request.getParameter("message");
            if (message != null && !message.isEmpty()) {
                request.setAttribute("message", URLDecoder.decode(message, StandardCharsets.UTF_8.toString()));
            }
            String errorMessage = request.getParameter("errorMessage");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                request.setAttribute("errorMessage", URLDecoder.decode(errorMessage, StandardCharsets.UTF_8.toString()));
            }

            request.getRequestDispatcher("/carrello.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Errore nel caricamento del carrello: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean loggedUser = (UtenteBean) session.getAttribute("user");
        String action = request.getParameter("action");

        try {
            CarrelloBean carrelloUtente = null;
            if (loggedUser != null) {
                carrelloUtente = carrelloDAO.doRetrieveByUtente(loggedUser.getEmail());
                if (carrelloUtente == null) {
                    carrelloUtente = new CarrelloBean();
                    carrelloUtente.setIdUtente(loggedUser.getEmail());
                    carrelloDAO.doSave(carrelloUtente);
                    carrelloUtente = carrelloDAO.doRetrieveByUtente(loggedUser.getEmail());
                }
                session.setAttribute("carrello", carrelloUtente);
            }

            if ("aggiornaQuantita".equals(action)) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                Map<String, Object> responseMap = new LinkedHashMap<>();
                boolean cartModified = false;

                try {
                    Long idProdotto = Long.parseLong(request.getParameter("idProdotto"));
                    int newQuantity = Integer.parseInt(request.getParameter("quantita"));

                    ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(idProdotto);
                    if (prodotto == null) {
                        responseMap.put("success", false);
                        responseMap.put("message", "Prodotto non più esistente.");
                    } else if (newQuantity > prodotto.getDisponibilita()) {
                        responseMap.put("success", false);
                        responseMap.put("message", "Disponibilità non sufficiente! Solo " + prodotto.getDisponibilita() + " pezzi disponibili.");
                    } else {
                        if (loggedUser != null) {
                            CartItemBean itemToUpdate = cartItemDAO.doRetrieveByProdottoAndCarrello(idProdotto, carrelloUtente.getId());
                            if (itemToUpdate != null) {
                                if (newQuantity > 0) {
                                    itemToUpdate.setQuantita(newQuantity);
                                    cartItemDAO.doUpdate(itemToUpdate);
                                    responseMap.put("success", true);
                                    responseMap.put("message", "Quantità aggiornata!");
                                    cartModified = true;
                                } else {
                                    cartItemDAO.doDelete(itemToUpdate.getId());
                                    responseMap.put("success", true);
                                    responseMap.put("message", "Prodotto rimosso!");
                                    cartModified = true;
                                }
                            } else {
                                responseMap.put("success", false);
                                responseMap.put("message", "Prodotto non trovato nel carrello.");
                            }
                        } else {
                            Map<Long, Integer> guestCart = getGuestCartFromCookies(request);
                            if (guestCart.containsKey(idProdotto)) {
                                if (newQuantity > 0) {
                                    guestCart.put(idProdotto, newQuantity);
                                    responseMap.put("success", true);
                                    responseMap.put("message", "Quantità aggiornata nel carrello guest!");
                                    cartModified = true;
                                } else {
                                    guestCart.remove(idProdotto);
                                    responseMap.put("success", true);
                                    responseMap.put("message", "Prodotto rimosso dal carrello guest!");
                                    cartModified = true;
                                }
                                saveGuestCartToCookies(response, guestCart);
                            } else {
                                responseMap.put("success", false);
                                responseMap.put("message", "Prodotto non trovato nel carrello guest.");
                            }
                        }
                    }
                } catch (SQLException | NumberFormatException e) {
                    responseMap.put("success", false);
                    responseMap.put("message", "Errore nei dati inviati o nel database.");
                }

                if (cartModified) {
                    session.setAttribute("cartCount", getCartItemCount(request, loggedUser != null ? loggedUser.getEmail() : null));
                }

                response.getWriter().write(gson.toJson(responseMap));
                return;
            }

            if ("aggiungi".equals(action)) {
                Long idProdotto = Long.parseLong(request.getParameter("idProdotto"));
                int quantita = Integer.parseInt(request.getParameter("quantita"));
                ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(idProdotto);
                if (prodotto != null) {
                    int quantitaDisponibile = prodotto.getDisponibilita();
                    boolean cartModified = false;

                    if (loggedUser != null) {
                        CartItemBean existingItem = cartItemDAO.doRetrieveByProdottoAndCarrello(idProdotto, carrelloUtente.getId());
                        int quantitaNelCarrello = (existingItem != null) ? existingItem.getQuantita() : 0;
                        int quantitaDopoAggiunta = quantitaNelCarrello + quantita;

                        if (quantitaDopoAggiunta > quantitaDisponibile) {
                            session.setAttribute("errorMessage", "Disponibilità non sufficiente! Solo " + quantitaDisponibile + " pezzi disponibili.");
                        } else {
                            if (existingItem != null) {
                                existingItem.setQuantita(quantitaDopoAggiunta);
                                cartItemDAO.doUpdate(existingItem);
                                session.setAttribute("message", "Quantità aggiornata!");
                            } else {
                                CartItemBean newItem = new CartItemBean(idProdotto, carrelloUtente.getId(), quantita, prodotto.isPersonalizzabile(), prodotto.getImgPath());
                                cartItemDAO.doSave(newItem);
                                session.setAttribute("message", "Prodotto aggiunto!");
                                cartModified = true; // Nuovo elemento aggiunto, il conteggio potrebbe aumentare
                            }
                        }
                    } else {
                        Map<Long, Integer> guestCart = getGuestCartFromCookies(request);
                        int quantitaNelCarrelloGuest = guestCart.getOrDefault(idProdotto, 0);
                        int quantitaDopoAggiuntaGuest = quantitaNelCarrelloGuest + quantita;

                        if (quantitaDopoAggiuntaGuest > quantitaDisponibile) {
                            session.setAttribute("errorMessage", "Disponibilità non sufficiente! Solo " + quantitaDisponibile + " pezzi disponibili.");
                        } else {
                            if (!guestCart.containsKey(idProdotto)) { // Se l'articolo è nuovo nel carrello guest
                                cartModified = true;
                            }
                            guestCart.put(idProdotto, quantitaDopoAggiuntaGuest);
                            saveGuestCartToCookies(response, guestCart);
                            session.setAttribute("message", "Prodotto aggiunto al carrello guest!");
                        }
                    }
                    if (cartModified) { // Aggiorna il contatore solo se un nuovo elemento distinto è stato aggiunto
                        session.setAttribute("cartCount", getCartItemCount(request, loggedUser != null ? loggedUser.getEmail() : null));
                    }
                } else {
                    session.setAttribute("errorMessage", "Prodotto non trovato!");
                }
            } else if ("rimuovi".equals(action)) {
                Long idProdotto = Long.parseLong(request.getParameter("idProdotto"));
                boolean cartModified = false;

                if (loggedUser != null) {
                    CartItemBean itemToRemove = cartItemDAO.doRetrieveByProdottoAndCarrello(idProdotto, carrelloUtente.getId());
                    if (itemToRemove != null) {
                        cartItemDAO.doDelete(itemToRemove.getId());
                        session.setAttribute("message", "Prodotto rimosso!");
                        cartModified = true;
                    } else {
                        session.setAttribute("errorMessage", "Prodotto non trovato nel carrello.");
                    }
                } else {
                    Map<Long, Integer> guestCart = getGuestCartFromCookies(request);
                    if (guestCart.containsKey(idProdotto)) {
                        guestCart.remove(idProdotto);
                        saveGuestCartToCookies(response, guestCart);
                        session.setAttribute("message", "Prodotto rimosso dal carrello guest!");
                        cartModified = true;
                    } else {
                        session.setAttribute("errorMessage", "Prodotto non trovato nel carrello guest.");
                    }
                }
                if (cartModified) { // Aggiorna il contatore solo se un elemento è stato rimosso
                    session.setAttribute("cartCount", getCartItemCount(request, loggedUser != null ? loggedUser.getEmail() : null));
                }
            }

            response.sendRedirect(request.getContextPath() + "/CarrelloServlet");

        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Errore DB: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dati input non validi.");
            response.sendRedirect(request.getContextPath() + "/CarrelloServlet");
        }
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
                    }
                    break;
                }
            }
        }
        return guestCart;
    }

    private void saveGuestCartToCookies(HttpServletResponse response, Map<Long, Integer> guestCart) {
        String cartJsonString = gson.toJson(guestCart);

        Cookie cookie = new Cookie("guest_cart", URLEncoder.encode(cartJsonString, StandardCharsets.UTF_8));
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static void mergeGuestCartToUserCart(HttpServletRequest request, HttpServletResponse response, String userEmail) throws SQLException, IOException {
        CarrelloDAO carrelloDAO = new CarrelloDAO();
        CartItemDAO cartItemDAO = new CartItemDAO();
        ProdottoDAO prodottoDAO = new ProdottoDAO();
        Gson gson = new Gson();

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
                    }
                    break;
                }
            }
        }


        if (!guestCart.isEmpty()) {
            CarrelloBean userCarrello = carrelloDAO.doRetrieveByUtente(userEmail);
            if (userCarrello == null) {
                userCarrello = new CarrelloBean();
                userCarrello.setIdUtente(userEmail);
                carrelloDAO.doSave(userCarrello);
                userCarrello = carrelloDAO.doRetrieveByUtente(userEmail);
            }

            boolean merged = false;
            for (Map.Entry<Long, Integer> guestEntry : guestCart.entrySet()) {
                Long idProdottoGuest = guestEntry.getKey();
                int quantitaGuest = guestEntry.getValue();

                CartItemBean existingUserItem = cartItemDAO.doRetrieveByProdottoAndCarrello(idProdottoGuest, userCarrello.getId());

                ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(idProdottoGuest);
                if (prodotto == null) {
                    continue;
                }

                int quantitaDisponibile = prodotto.getDisponibilita();
                int quantitaFinale = quantitaGuest;

                if (existingUserItem != null) {
                    quantitaFinale += existingUserItem.getQuantita();
                }

                if (quantitaFinale > quantitaDisponibile) {
                    quantitaFinale = quantitaDisponibile;
                    request.setAttribute("errorMessage", (request.getAttribute("errorMessage") != null ? request.getAttribute("errorMessage") + ". " : "") + "Attenzione: la quantità di " + prodotto.getNome() + " è stata limitata a " + quantitaDisponibile + " a causa della disponibilità.");
                }

                if (existingUserItem != null) {
                    existingUserItem.setQuantita(quantitaFinale);
                    cartItemDAO.doUpdate(existingUserItem);
                    merged = true;
                } else {
                    CartItemBean newItem = new CartItemBean(
                            idProdottoGuest,
                            userCarrello.getId(),
                            quantitaFinale,
                            prodotto.isPersonalizzabile(),
                            prodotto.getImgPath()
                    );
                    cartItemDAO.doSave(newItem);
                    merged = true;
                }
            }

            if (merged) {
                Cookie cookie = new Cookie("guest_cart", "");
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
                request.setAttribute("message", (request.getAttribute("message") != null ? request.getAttribute("message") + ". " : "") + "Carrello guest unito al tuo account!");
            }
        }
    }

    public static int getCartItemCount(HttpServletRequest request, String userEmail) throws SQLException {
        CarrelloDAO staticCarrelloDAO = new CarrelloDAO();
        CartItemDAO staticCartItemDAO = new CartItemDAO();

        Gson staticGson = new Gson();
        Map<Long, Integer> guestCartFromStatic = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("guest_cart".equals(cookie.getName())) {
                    try {
                        String cartJsonString = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                        Type type = new TypeToken<Map<Long, Integer>>() {}.getType();
                        guestCartFromStatic = staticGson.fromJson(cartJsonString, type);
                        if (guestCartFromStatic == null) {
                            guestCartFromStatic = new HashMap<>();
                        }
                    } catch (Exception ignored) {
                    }
                    break;
                }
            }
        }

        if (userEmail != null && !userEmail.isEmpty()) {
            CarrelloBean userCarrello = staticCarrelloDAO.doRetrieveByUtente(userEmail);
            if (userCarrello != null) {
                List<CartItemBean> rawCartItems = staticCartItemDAO.doRetrieveByCarrello(userCarrello.getId());
                return rawCartItems.size();
            }
            return 0;
        } else {
            return guestCartFromStatic.size();
        }
    }
}