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
import java.util.Optional;

@WebServlet("/AdminUploadServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class AdminUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String UPLOAD_DIRECTORY = "uploads";

    public AdminUploadServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
       String currentUser = (String) session.getAttribute("currentUser");


        GruppoProdottiDAO gruppoDAO = new GruppoProdottiDAO();
        GruppoProdottiBean currentSelectedGroup = null;

        try {
            List<GruppoProdottiBean> allGroups = gruppoDAO.doRetrieveAll();
            if (allGroups != null && !allGroups.isEmpty()) {
                allGroups.sort((g1, g2) -> g2.getId().compareTo(g1.getId()));
                currentSelectedGroup = allGroups.get(0); // Seleziona il gruppo più recente o uno di default
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL nel recupero dei gruppi prodotti per doGet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Impossibile caricare i gruppi di prodotti. Riprova.");
        }

        request.setAttribute("currentSelectedGroup", currentSelectedGroup);
        request.setAttribute("oldProductName", "");
        request.setAttribute("oldProductDescription", "");
        request.setAttribute("oldProductTaglia", "");
        request.setAttribute("oldProductColore", "");
        request.setAttribute("oldProductCodiceColore", ""); // Nuovo campo
        request.setAttribute("oldProductCategory", "");
        request.setAttribute("oldProductPrice", "");
        request.setAttribute("oldProductIva", "");
        request.setAttribute("oldProductDisponibilita", "");
        request.setAttribute("oldProductPersonalizzabile", false);
        request.setAttribute("oldGroupName", currentSelectedGroup != null ? currentSelectedGroup.getNome() : "");

        request.getRequestDispatcher("/adminUpload.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String currentUser = (String) session.getAttribute("currentUser");

        // Qui dovresti aggiungere un controllo sui permessi dell'utente, ad esempio:
        // if (currentUser == null || !currentUser.isAdmin()) {
        //     response.sendRedirect(request.getContextPath() + "/login.jsp"); // O una pagina di errore
        //     return;
        // }

        List<String> validationErrors = new ArrayList<>();
        String successMessage = null;
        GruppoProdottiBean currentSelectedGroup = null;

        String groupName = request.getParameter("groupName");
        String groupIdStr = request.getParameter("groupId");

        // Imposta gli attributi per ripopolare il form in caso di errore
        request.setAttribute("oldProductName", request.getParameter("productName"));
        request.setAttribute("oldProductDescription", request.getParameter("productDescription"));
        request.setAttribute("oldProductTaglia", request.getParameter("productTaglia"));
        request.setAttribute("oldProductColore", request.getParameter("productColore"));
        request.setAttribute("oldProductCodiceColore", request.getParameter("productCodiceColore")); // Nuovo campo
        request.setAttribute("oldProductCategory", request.getParameter("productCategoria"));
        request.setAttribute("oldProductPrice", request.getParameter("productPrezzo"));
        request.setAttribute("oldProductIva", request.getParameter("productIVA"));
        request.setAttribute("oldProductDisponibilita", request.getParameter("productDisponibilita"));
        request.setAttribute("oldProductPersonalizzabile", "true".equals(request.getParameter("personalizzabile")));
        request.setAttribute("oldGroupName", groupName);

        GruppoProdottiDAO gruppoDAO = new GruppoProdottiDAO();
        ProdottoDAO prodottoDAO = new ProdottoDAO();

        try {
            List<GruppoProdottiBean> allGroups = gruppoDAO.doRetrieveAll();

            if (groupIdStr != null && !groupIdStr.trim().isEmpty()) {
                try {
                    Long groupId = Long.parseLong(groupIdStr);
                    Optional<GruppoProdottiBean> foundGroup = allGroups.stream()
                            .filter(g -> g.getId().equals(groupId))
                            .findFirst();

                    if (foundGroup.isPresent()) {
                        currentSelectedGroup = foundGroup.get();
                        if (!currentSelectedGroup.getNome().equalsIgnoreCase(groupName.trim())) {
                            validationErrors.add("Nome del gruppo non corrisponde all'ID esistente. Errore interno.");
                        }
                    } else {
                        validationErrors.add("Il gruppo selezionato non esiste più. Si prega di riprovare.");
                        response.sendRedirect(request.getContextPath() + "/AdminUploadServlet");
                        return;
                    }

                } catch (NumberFormatException e) {
                    validationErrors.add("ID gruppo prodotto non valido. Riprovare.");
                }
            } else { // Se non è stato selezionato un gruppo esistente, tenta di crearne uno nuovo
                if (groupName == null || groupName.trim().isEmpty()) {
                    validationErrors.add("Il nome del gruppo non può essere vuoto.");
                } else if (groupName.length() > 255) {
                    validationErrors.add("Il nome del gruppo è troppo lungo (max 255 caratteri).");
                }

                if (validationErrors.isEmpty()) {
                    Optional<GruppoProdottiBean> foundGroup = allGroups.stream()
                            .filter(g -> g.getNome().equalsIgnoreCase(groupName.trim()))
                            .findFirst();

                    if (foundGroup.isPresent()) {
                        currentSelectedGroup = foundGroup.get();
                    } else {
                        GruppoProdottiBean newGroup = new GruppoProdottiBean();
                        newGroup.setNome(groupName.trim());
                        gruppoDAO.doSave(newGroup); // Salva il nuovo gruppo
                        currentSelectedGroup = newGroup;
                        successMessage = "Gruppo prodotto '" + groupName + "' creato con successo! ";
                    }
                }
            }

            // Se ci sono errori nella gestione del gruppo, reindirizza
            if (!validationErrors.isEmpty()) {
                request.setAttribute("validationErrors", validationErrors);
                request.setAttribute("currentSelectedGroup", currentSelectedGroup); // Mantiene il gruppo selezionato/tentato
                request.getRequestDispatcher("/adminUpload.jsp").forward(request, response);
                return;
            }

            // Assicurati che currentSelectedGroup sia non nullo prima di procedere
            if (currentSelectedGroup == null) {
                validationErrors.add("Errore critico nella gestione del gruppo prodotto.");
                request.setAttribute("validationErrors", validationErrors);
                request.getRequestDispatcher("/adminUpload.jsp").forward(request, response);
                return;
            }


            String productName = request.getParameter("productName");
            String productDescription = request.getParameter("productDescription");
            String productTaglia = request.getParameter("productTaglia");
            String productColore = request.getParameter("productColore");
            String productCodiceColore = request.getParameter("productCodiceColore"); // Nuovo parametro
            String productCategoria = request.getParameter("productCategoria");
            String productPrezzoStr = request.getParameter("productPrezzo");
            String productIvaStr = request.getParameter("productIVA");
            String productDisponibilitaStr = request.getParameter("productDisponibilita");
            String personalizzabileStr = request.getParameter("personalizzabile");

            String productImgPath = null;
            Part filePart = null;

            try {
                filePart = request.getPart("productImgFile");
                String fileName = getFileName(filePart);

                long fileSize = filePart.getSize();
                String contentType = filePart.getContentType();

                if (fileName == null || fileName.isEmpty()) {
                    validationErrors.add("Devi caricare un'immagine per il prodotto.");
                } else if (fileSize == 0) {
                    validationErrors.add("Il file dell'immagine è vuoto.");
                } else if (!contentType.startsWith("image/")) {
                    validationErrors.add("Il file caricato non è un'immagine valida (sono accettate solo immagini).");
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

            // Validazioni campi prodotto
            if (productName == null || productName.trim().isEmpty()) {
                validationErrors.add("Il nome del prodotto è obbligatorio.");
            } else if (productName.length() > 255) {
                validationErrors.add("Il nome del prodotto è troppo lungo (max 255 caratteri).");
            }
            if (productDescription == null || productDescription.trim().isEmpty()) {
                validationErrors.add("La descrizione è obbligatoria.");
            } else if (productDescription.length() > 4096) {
                validationErrors.add("La descrizione è troppo lunga (max 4096 caratteri).");
            }
            if (productTaglia == null || productTaglia.trim().isEmpty()) {
                validationErrors.add("La taglia è obbligatoria.");
            }
            if (productColore == null || productColore.trim().isEmpty()) {
                validationErrors.add("Il colore è obbligatorio.");
            } else if (productColore.length() > 50) {
                validationErrors.add("Il colore è troppo lungo (max 50 caratteri).");
            }

            // Validazione per codiceColore
            if (productCodiceColore == null || productCodiceColore.trim().isEmpty()) {
                validationErrors.add("Il codice colore è obbligatorio.");
            } else if (!productCodiceColore.matches("^#([A-Fa-f0-9]{6})$")) { // Regex per #RRGGBB
                validationErrors.add("Il codice colore non è valido. Deve essere nel formato #RRGGBB (es. #FF0000).");
            }

            if (productCategoria == null || productCategoria.trim().isEmpty()) {
                validationErrors.add("La categoria è obbligatoria.");
            } else if (productCategoria.length() > 50) {
                validationErrors.add("La categoria è troppo lunga (max 50 caratteri).");
            }

            float prezzo = 0;
            try {
                prezzo = Float.parseFloat(productPrezzoStr);
                if (prezzo <= 0) validationErrors.add("Il prezzo deve essere un valore positivo.");
            } catch (NumberFormatException e) {
                validationErrors.add("Prezzo non valido. Inserire un numero valido.");
            }

            int iva = 0;
            try {
                iva = Integer.parseInt(productIvaStr);
                if (iva < 0 || iva > 100) validationErrors.add("IVA non valida. Selezionare una percentuale tra 0 e 100.");
            } catch (NumberFormatException e) {
                validationErrors.add("IVA non valida. Inserire un numero intero valido.");
            }

            int disponibilita = 0;
            try {
                disponibilita = Integer.parseInt(productDisponibilitaStr);
                if (disponibilita < 0) validationErrors.add("La disponibilità non può essere negativa.");
            } catch (NumberFormatException e) {
                validationErrors.add("Disponibilità non valida. Inserire un numero intero valido.");
            }

            boolean personalizzabile = "true".equals(personalizzabileStr);

            // Se non ci sono errori di validazione, procedi con il salvataggio del prodotto
            if (validationErrors.isEmpty()) {
                try {
                    ProdottoBean newProduct = new ProdottoBean();

                    newProduct.setNome(productName.trim());
                    newProduct.setDescrizione(productDescription.trim());
                    newProduct.setTaglia(productTaglia);
                    newProduct.setColore(productColore.trim());
                    newProduct.setCodiceColore(productCodiceColore.trim()); // Imposta il nuovo campo
                    newProduct.setCategoria(productCategoria.trim());
                    newProduct.setPrezzo(prezzo);
                    newProduct.setIva(iva);
                    newProduct.setDisponibilita(disponibilita);
                    newProduct.setPersonalizzabile(personalizzabile);
                    newProduct.setImgPath(productImgPath);
                    newProduct.setPublisher(currentUser); // Assicurati che currentUser non sia null
                    newProduct.setGruppo(currentSelectedGroup.getId());


                    prodottoDAO.doSave(newProduct);
                    successMessage = (successMessage == null ? "" : successMessage + "<br>") + "Prodotto '" + productName + "' aggiunto con successo al gruppo '" + currentSelectedGroup.getNome() + "'!";

                    // Resetta i campi del form dopo un salvataggio riuscito
                    request.setAttribute("oldProductName", "");
                    request.setAttribute("oldProductDescription", "");
                    request.setAttribute("oldProductTaglia", "");
                    request.setAttribute("oldProductColore", "");
                    request.setAttribute("oldProductCodiceColore", ""); // Resetta il nuovo campo
                    request.setAttribute("oldProductCategory", "");
                    request.setAttribute("oldProductPrice", "");
                    request.setAttribute("oldProductIva", "");
                    request.setAttribute("oldProductDisponibilita", "");
                    request.setAttribute("oldProductPersonalizzabile", false);

                } catch (SQLException e) {
                    System.err.println("Errore SQL nell'aggiunta del prodotto: " + e.getMessage());
                    e.printStackTrace();
                    validationErrors.add("Errore database nell'aggiunta del prodotto. Riprova.");
                    // Cerca di eliminare l'immagine caricata se c'è un errore DB
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

        } catch (SQLException e) {
            System.err.println("Errore SQL generale nella servlet (gruppo o altro): " + e.getMessage());
            e.printStackTrace();
            validationErrors.add("Errore interno del server. Riprova.");
        } finally {
            request.setAttribute("validationErrors", validationErrors);
            if (successMessage != null) {
                request.setAttribute("successMessage", successMessage);
            }
            request.setAttribute("currentSelectedGroup", currentSelectedGroup); // Assicura che il gruppo sia sempre settato per la JSP

            request.getRequestDispatcher("adminUpload.jsp").forward(request, response);
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
}