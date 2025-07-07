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

@WebServlet("/AdminUpdateProductQuantityServlet")
public class AdminUpdateProductQuantityServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AdminUpdateProductQuantityServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdParam = request.getParameter("productId");
        String newQuantityParam = request.getParameter("newQuantity");
        String redirectUrl = request.getContextPath() + "/adminEdit.jsp";

        Long productId = null;
        int newQuantity = -1;
        Long groupId = null;

        try {
            if (productIdParam == null || productIdParam.trim().isEmpty()) {
                response.sendRedirect(redirectUrl + "?errorMessage=" + URLEncoder.encode("ID prodotto non fornito.", "UTF-8"));
                return;
            }
            productId = Long.parseLong(productIdParam);

            if (newQuantityParam == null || newQuantityParam.trim().isEmpty()) {
                response.sendRedirect(redirectUrl + "?errorMessage=" + URLEncoder.encode("Quantità non fornita.", "UTF-8"));
                return;
            }
            newQuantity = Integer.parseInt(newQuantityParam);

            if (newQuantity < 0) {
                response.sendRedirect(redirectUrl + "?errorMessage=" + URLEncoder.encode("La quantità non può essere negativa.", "UTF-8"));
                return;
            }

            ProdottoDAO prodottoDAO = new ProdottoDAO();
            ProdottoBean prodottoToUpdate = prodottoDAO.doRetrieveByKey(productId);

            if (prodottoToUpdate == null) {
                response.sendRedirect(redirectUrl + "?errorMessage=" + URLEncoder.encode("Prodotto non trovato.", "UTF-8"));
                return;
            }
            groupId = prodottoToUpdate.getGruppo();

            prodottoToUpdate.setDisponibilita(newQuantity);
            prodottoDAO.doUpdate(prodottoToUpdate);

            String successMessage = "Quantità del prodotto '" + prodottoToUpdate.getNome() + "' aggiornata con successo a " + newQuantity + ".";
            redirectUrl += "?groupId=" + groupId + "&successMessage=" + URLEncoder.encode(successMessage, "UTF-8");
            response.sendRedirect(redirectUrl);

        } catch (NumberFormatException e) {
            System.err.println("NumberFormatException in AdminUpdateProductQuantityServlet: " + e.getMessage());
            e.printStackTrace();
            String errorMsg = "ID prodotto o quantità non validi. Assicurati che siano numeri interi.";
            if (groupId != null) {
                redirectUrl += "?groupId=" + groupId + "&errorMessage=" + URLEncoder.encode(errorMsg, "UTF-8");
            } else {
                redirectUrl += "?errorMessage=" + URLEncoder.encode(errorMsg, "UTF-8");
            }
            response.sendRedirect(redirectUrl);
        } catch (SQLException e) {
            System.err.println("Errore SQL in AdminUpdateProductQuantityServlet: " + e.getMessage());
            e.printStackTrace();
            String errorMsg = "Errore database durante l'aggiornamento della quantità. Riprova.";
            if (groupId != null) {
                redirectUrl += "?groupId=" + groupId + "&errorMessage=" + URLEncoder.encode(errorMsg, "UTF-8");
            } else {
                redirectUrl += "?errorMessage=" + URLEncoder.encode(errorMsg, "UTF-8");
            }
            response.sendRedirect(redirectUrl);
        }
    }
}