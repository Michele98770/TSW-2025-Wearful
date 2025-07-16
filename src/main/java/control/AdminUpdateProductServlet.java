package control;

import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.StringJoiner;

@WebServlet("/AdminUpdateProductServlet")
public class AdminUpdateProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AdminUpdateProductServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdParam = request.getParameter("productId");
        String newQuantityParam = request.getParameter("newQuantity");
        String newPriceParam = request.getParameter("newPrice");

        String redirectUrl = request.getContextPath() + "/adminEdit.jsp";

        Long productId = null;
        int newQuantity = -1;
        double newPrice = -1.0;
        Long groupId = null;

        try {
            if (productIdParam == null || productIdParam.trim().isEmpty()) {
                redirectToError(response, redirectUrl, null, "ID prodotto non fornito.");
                return;
            }
            productId = Long.parseLong(productIdParam);

            // Validazione Quantità
            if (newQuantityParam == null || newQuantityParam.trim().isEmpty()) {
                redirectToError(response, redirectUrl, null, "Quantità non fornita.");
                return;
            }
            newQuantity = Integer.parseInt(newQuantityParam);
            if (newQuantity < 0) {
                redirectToError(response, redirectUrl, null, "La quantità non può essere negativa.");
                return;
            }

            if (newPriceParam == null || newPriceParam.trim().isEmpty()) {
                redirectToError(response, redirectUrl, null, "Prezzo non fornito.");
                return;
            }
            newPrice = Double.parseDouble(newPriceParam.replace(',', '.')); // Sostituisce virgola con punto per sicurezza
            if (newPrice < 0) {
                redirectToError(response, redirectUrl, null, "Il prezzo non può essere negativo.");
                return;
            }

            ProdottoDAO prodottoDAO = new ProdottoDAO();
            ProdottoBean prodottoToUpdate = prodottoDAO.doRetrieveByKey(productId);

            if (prodottoToUpdate == null) {
                redirectToError(response, redirectUrl, null, "Prodotto non trovato.");
                return;
            }
            groupId = prodottoToUpdate.getGruppo();

            prodottoToUpdate.setDisponibilita(newQuantity);
            prodottoToUpdate.setPrezzo((float) newPrice);

            prodottoDAO.doUpdate(prodottoToUpdate);

            // Costruisce l'URL di successo in modo sicuro
            String successMessage = "Prodotto '" + prodottoToUpdate.getNome() + "' aggiornato con successo.";
            StringJoiner params = new StringJoiner("&", "?", "");
            // Aggiunge groupId solo se non è null
            if (groupId != null) {
                params.add("groupId=" + groupId);
            }
            params.add("successMessage=" + URLEncoder.encode(successMessage, "UTF-8"));

            response.sendRedirect(redirectUrl + params.toString());

        } catch (NumberFormatException e) {
            System.err.println("NumberFormatException in AdminUpdateProductServlet: " + e.getMessage());
            e.printStackTrace();
            redirectToError(response, redirectUrl, groupId, "ID prodotto, quantità o prezzo non validi. Assicurati che siano numeri corretti.");
        } catch (SQLException e) {
            System.err.println("Errore SQL in AdminUpdateProductServlet: " + e.getMessage());
            e.printStackTrace();
            redirectToError(response, redirectUrl, groupId, "Errore database durante l'aggiornamento del prodotto. Riprova.");
        }
    }


    private void redirectToError(HttpServletResponse response, String redirectUrl, Long groupId, String errorMessage) throws IOException {
        StringJoiner params = new StringJoiner("&", "?", "");
        if (groupId != null) {
            params.add("groupId=" + groupId);
        }
        params.add("errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
        response.sendRedirect(redirectUrl + params.toString());
    }
}