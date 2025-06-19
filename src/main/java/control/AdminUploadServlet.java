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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean currentUser = (UtenteBean) session.getAttribute("currentUser");



        GruppoProdottiDAO gruppoDAO = new GruppoProdottiDAO();
        GruppoProdottiBean currentSelectedGroup = null;

        try {
            // Recupera tutti i gruppi
            List<GruppoProdottiBean> allGroups = gruppoDAO.doRetrieveAll();
            if (allGroups != null && !allGroups.isEmpty()) {
                allGroups.sort((g1, g2) -> g2.getId().compareTo(g1.getId()));
                currentSelectedGroup = allGroups.get(0); // Prende il gruppo con l'ID più alto (presumibilmente l'ultimo creato)
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL nel recupero dei gruppi prodotti per doGet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Impossibile caricare i gruppi di prodotti. Riprova.");
        }

        request.setAttribute("currentSelectedGroup", currentSelectedGroup);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean currentUser = (UtenteBean) session.getAttribute("currentUser");


        List<String> validationErrors = new ArrayList<>();
        String successMessage = null;
        GruppoProdottiBean currentSelectedGroup = null;

        String groupName = request.getParameter("groupName");
        String groupIdStr = request.getParameter("groupId");

        // Reintegro i dati del form per ripopolare in caso di errore
        request.setAttribute("oldProductName", request.getParameter("productName"));
        request.setAttribute("oldProductDescription", request.getParameter("productDescription"));
        request.setAttribute("oldProductTaglia", request.getParameter("productTaglia"));
        request.setAttribute("oldProductColore", request.getParameter("productColore"));
        request.setAttribute("oldProductCategory", request.getParameter("productCategoria"));
        request.setAttribute("oldProductPrice", request.getParameter("productPrezzo"));
        request.setAttribute("oldProductIva", request.getParameter("productIVA"));
        request.setAttribute("oldProductDisponibilita", request.getParameter("productDisponibilita"));
        request.setAttribute("oldProductPersonalizzabile", "true".equals(request.getParameter("personalizzabile")));
        request.setAttribute("oldGroupName", groupName); // Mantiene il nome del gruppo digitato in caso di errore

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
            } else {

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
                        gruppoDAO.doSave(newGroup); // Questo popolerà l'ID nel bean
                        currentSelectedGroup = newGroup; // Ora currentSelectedGroup contiene l'ID
                        successMessage = "Gruppo prodotto '" + groupName + "' creato con successo!";
                    }
                }
            }

            if (currentSelectedGroup == null && validationErrors.isEmpty()) {
                validationErrors.add("Impossibile determinare o creare il gruppo prodotto.");
            }

            if (!validationErrors.isEmpty()) {
                request.setAttribute("validationErrors", validationErrors);
                request.setAttribute("currentSelectedGroup", currentSelectedGroup); // Passa il gruppo (anche se nullo o con errori) per mantenere lo stato
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return;
            }

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
            } catch (NumberFormatException e) {
                validationErrors.add("IVA non valida. Selezionare una percentuale valida.");
            }

            int disponibilita = 0;
            try {
                disponibilita = Integer.parseInt(productDisponibilitaStr);
                if (disponibilita < 0) validationErrors.add("La disponibilità non può essere negativa.");
            } catch (NumberFormatException e) {
                validationErrors.add("Disponibilità non valida. Inserire un numero intero valido.");
            }

            boolean personalizzabile = "true".equals(personalizzabileStr);

            if (validationErrors.isEmpty()) {
                try {
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
                    newProduct.setPublisher("admin@wearful.com");
                    newProduct.setGruppo(currentSelectedGroup.getId());


                    System.out.println("DEBUG: Valore IVA prima del salvataggio: " + newProduct.getIva());
                    System.out.println("DEBUG: Valore Publisher prima del salvataggio: " + newProduct.getPublisher());
                    System.out.println("DEBUG: Valore Gruppo ID prima del salvataggio: " + newProduct.getGruppo() + " (Nome: " + currentSelectedGroup.getNome() + ")");

                    prodottoDAO.doSave(newProduct);
                    successMessage = (successMessage == null ? "" : successMessage + "<br>") + "Prodotto '" + productName + "' aggiunto con successo al gruppo '" + currentSelectedGroup.getNome() + "'!";

                    request.removeAttribute("oldProductName");
                    request.removeAttribute("oldProductDescription");
                    request.removeAttribute("oldProductTaglia");
                    request.removeAttribute("oldProductColore");
                    request.removeAttribute("oldProductCategory");
                    request.removeAttribute("oldProductPrice");
                    request.removeAttribute("oldProductIva");
                    request.removeAttribute("oldProductDisponibilita");
                    request.removeAttribute("oldProductPersonalizzabile");

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

        } catch (SQLException e) {
            System.err.println("Errore SQL generale nella servlet: " + e.getMessage());
            e.printStackTrace();
            validationErrors.add("Errore interno del server. Riprova.");
        } finally {
            request.setAttribute("validationErrors", validationErrors);
            if (successMessage != null) {
                request.setAttribute("successMessage", successMessage);
            }

            request.setAttribute("currentSelectedGroup", currentSelectedGroup);

            request.getRequestDispatcher("index.jsp").forward(request, response);
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