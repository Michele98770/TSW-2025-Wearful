package control;

import model.gruppoprodotti.GruppoProdottiBean;
import model.gruppoprodotti.GruppoProdottiDAO;
import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;
import model.utente.UtenteBean;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/AdminUploadServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class AdminUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String UPLOAD_DIRECTORY = "uploads";

    public AdminUploadServlet() {
        super();
    }

    // Questo metodo sarà chiamato per visualizzare la pagina con i form
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean currentUser = (UtenteBean) session.getAttribute("currentUser");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized"); // Modificato qui
            return;
        }

        // Carica la lista dei gruppi prodotti per il dropdown nella JSP
        try {
            GruppoProdottiDAO gruppoDAO = new GruppoProdottiDAO();
            List<GruppoProdottiBean> gruppiProdotti = gruppoDAO.doRetrieveAll();
            request.setAttribute("gruppiProdotti", gruppiProdotti); // Passa i gruppi alla JSP
        } catch (SQLException e) {
            System.err.println("Errore SQL nel recupero dei gruppi prodotti per doGet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Impossibile caricare i gruppi di prodotti. Riprova.");
        }

        request.getRequestDispatcher("/admin_upload.jsp").forward(request, response); // Modificato qui
    }

    // Questo metodo sarà chiamato quando i form vengono sottomessi
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean currentUser = (UtenteBean) session.getAttribute("currentUser");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized"); // Modificato qui
            return;
        }

        String formType = request.getParameter("formType");
        List<String> validationErrors = new ArrayList<>();
        String successMessage = null; // Inizializza per la nuova richiesta

        if ("newGroup".equals(formType)) {
            String groupName = request.getParameter("groupName");

            if (groupName == null || groupName.trim().isEmpty()) {
                validationErrors.add("Il nome del gruppo non può essere vuoto.");
            } else if (groupName.length() > 255) {
                validationErrors.add("Il nome del gruppo è troppo lungo.");
            }

            if (validationErrors.isEmpty()) {
                try {
                    GruppoProdottiDAO gruppoDAO = new GruppoProdottiDAO();
                    List<GruppoProdottiBean> existingGroups = gruppoDAO.doRetrieveAll();
                    boolean exists = existingGroups.stream().anyMatch(g -> g.getNome().equalsIgnoreCase(groupName.trim()));

                    if (exists) {
                        validationErrors.add("Un gruppo con questo nome esiste già.");
                    } else {
                        GruppoProdottiBean newGroup = new GruppoProdottiBean();
                        newGroup.setNome(groupName.trim());
                        gruppoDAO.doSave(newGroup);
                        successMessage = "Gruppo prodotto '" + groupName + "' creato con successo!";
                    }
                } catch (SQLException e) {
                    System.err.println("Errore SQL nella creazione del gruppo: " + e.getMessage());
                    e.printStackTrace();
                    validationErrors.add("Errore database nella creazione del gruppo. Riprova.");
                }
            }
            // Mantiene il valore del campo per il form nel caso di errore
            request.setAttribute("oldGroupName", groupName);

        } else if ("newProduct".equals(formType)) {
            String productGroupStr = request.getParameter("productGroup");
            String productName = request.getParameter("productName");
            String productDescription = request.getParameter("productDescription");
            String productTaglia = request.getParameter("productTaglia");
            String productColore = request.getParameter("productColore");
            String productCategoria = request.getParameter("productCategoria");
            String productPrezzoStr = request.getParameter("productPrezzo");
            String productIvaStr = request.getParameter("productIVA");
            String productDisponibilitaStr = request.getParameter("productDisponibilita");
            String personalizzabileStr = request.getParameter("personalizzabile");

            String productImgPath = null;
            Part filePart = null;

            // Precompilazione per errore, mantiene i valori nel form del prodotto
            request.setAttribute("oldProductName", productName);
            request.setAttribute("oldProductDescription", productDescription);
            request.setAttribute("oldProductColor", productColore);
            request.setAttribute("oldProductCategory", productCategoria);
            request.setAttribute("oldProductPrice", productPrezzoStr);
            request.setAttribute("oldProductIva", productIvaStr);
            request.setAttribute("oldProductDisponibilita", productDisponibilitaStr);
            request.setAttribute("oldProductPersonalizzabile", "true".equals(personalizzabileStr));
            Long productGroupId = null;

            // Gestione upload file immagine
            try {
                filePart = request.getPart("productImgFile");
                String fileName = getFileName(filePart);

                // Validazione dimensione e tipo file
                long fileSize = filePart.getSize();
                String contentType = filePart.getContentType();

                if (fileName == null || fileName.isEmpty()) {
                    validationErrors.add("Devi caricare un'immagine per il prodotto.");
                } else if (fileSize == 0) {
                    validationErrors.add("Il file dell'immagine è vuoto.");
                } else if (!contentType.startsWith("image/")) {
                    validationErrors.add("Il file caricato non è un'immagine valida.");
                } else {
                    String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
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
                    }
                    productImgPath = request.getContextPath() + "/" + UPLOAD_DIRECTORY + "/" + uniqueFileName;
                }
            } catch (ServletException | IOException e) {
                System.err.println("Errore nell'upload del file: " + e.getMessage());
                e.printStackTrace();
                validationErrors.add("Errore nel caricamento dell'immagine: " + e.getMessage());
            }

            // Validazione campi prodotto (il resto)
            if (productGroupStr == null || productGroupStr.trim().isEmpty()) {
                validationErrors.add("Selezionare un gruppo prodotto.");
            } else {
                try {
                    productGroupId = Long.parseLong(productGroupStr);
                    request.setAttribute("oldProductGruppoId", productGroupId); // Mantiene il gruppo selezionato
                } catch (NumberFormatException e) {
                    validationErrors.add("ID gruppo prodotto non valido.");
                }
            }
            if (productName == null || productName.trim().isEmpty()) {
                validationErrors.add("Il nome del prodotto è obbligatorio.");
            } else if (productName.length() > 255) {
                validationErrors.add("Il nome del prodotto è troppo lungo.");
            }
            if (productDescription == null || productDescription.trim().isEmpty()) {
                validationErrors.add("La descrizione è obbligatoria.");
            } else if (productDescription.length() > 4096) {
                validationErrors.add("La descrizione è troppo lunga.");
            }
            if (productTaglia == null || productTaglia.trim().isEmpty()) {
                validationErrors.add("La taglia è obbligatoria.");
            }
            if (productColore == null || productColore.trim().isEmpty()) {
                validationErrors.add("Il colore è obbligatorio.");
            } else if (productColore.length() > 50) {
                validationErrors.add("Il colore è troppo lungo.");
            }
            if (productCategoria == null || productCategoria.trim().isEmpty()) {
                validationErrors.add("La categoria è obbligatoria.");
            } else if (productCategoria.length() > 50) {
                validationErrors.add("La categoria è troppo lunga.");
            }

            float prezzo = 0;
            try {
                prezzo = Float.parseFloat(productPrezzoStr);
                if (prezzo <= 0) validationErrors.add("Il prezzo deve essere positivo.");
            } catch (NumberFormatException e) {
                validationErrors.add("Prezzo non valido.");
            }

            int iva = 0;
            try {
                iva = Integer.parseInt(productIvaStr);
            } catch (NumberFormatException e) {
                validationErrors.add("IVA non valida.");
            }

            int disponibilita = 0;
            try {
                disponibilita = Integer.parseInt(productDisponibilitaStr);
                if (disponibilita < 0) validationErrors.add("La disponibilità non può essere negativa.");
            } catch (NumberFormatException e) {
                validationErrors.add("Disponibilità non valida.");
            }

            boolean personalizzabile = "true".equals(personalizzabileStr);

            if (validationErrors.isEmpty()) {
                try {
                    ProdottoDAO prodottoDAO = new ProdottoDAO();
                    ProdottoBean newProduct = new ProdottoBean();

                    newProduct.setNome(productName.trim());
                    newProduct.setDescrizione(productDescription.trim());
                    newProduct.setTaglia(productTaglia);
                    newProduct.setColore(productColore.trim());
                    newProduct.setCategoria(productCategoria.trim());
                    newProduct.setPrezzo(prezzo);
                    newProduct.setIva(iva);
                    newProduct.setDisponibilita(disponibilita);
                    newProduct.setPersonalizzabile(personalizzabile);
                    newProduct.setImgPath(productImgPath);
                    newProduct.setPublisher(currentUser.getEmail());
                    newProduct.setGruppo(productGroupId);

                    prodottoDAO.doSave(newProduct);
                    successMessage = "Prodotto '" + productName + "' aggiunto con successo!";
                    // Se l'operazione ha successo, potresti voler azzerare i campi del form prodotto
                    // per evitare che appaiano precompilati nel successivo tentativo di aggiunta.
                    // request.removeAttribute("oldProductName"); ecc...
                } catch (SQLException e) {
                    System.err.println("Errore SQL nell'aggiunta del prodotto: " + e.getMessage());
                    e.printStackTrace();
                    validationErrors.add("Errore database nell'aggiunta del prodotto. Riprova.");
                    if (productImgPath != null) {
                        try {
                            String relativeUploadPath = productImgPath.substring(request.getContextPath().length() + 1);
                            Path uploadedFilePath = Paths.get(getServletContext().getRealPath(""), relativeUploadPath);
                            Files.deleteIfExists(uploadedFilePath);
                        } catch (IOException ioE) {
                            System.err.println("Errore durante la cancellazione del file caricato dopo errore DB: " + ioE.getMessage());
                        }
                    }
                }
            }
        } else {
            validationErrors.add("Tipo di form non valido.");
        }

        // Ricarica la lista dei gruppi per il dropdown, sia in caso di successo che di errore
        try {
            GruppoProdottiDAO gruppoDAO = new GruppoProdottiDAO();
            List<GruppoProdottiBean> gruppiProdotti = gruppoDAO.doRetrieveAll();
            request.setAttribute("gruppiProdotti", gruppiProdotti);
        } catch (SQLException e) {
            System.err.println("Errore SQL nel recupero dei gruppi prodotti post-submit: " + e.getMessage());
            e.printStackTrace();
            validationErrors.add("Impossibile ricaricare i gruppi di prodotti. Riprova.");
        }


        // Imposta i messaggi di feedback e gli errori di validazione
        request.setAttribute("validationErrors", validationErrors);
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
        }

        // Fa il forward alla stessa pagina JSP
        request.getRequestDispatcher("/admin_upload.jsp").forward(request, response); // Modificato qui
    }

    // Metodo helper per estrarre il nome del file da Part
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
}