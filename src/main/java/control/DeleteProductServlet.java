package control;

import model.gruppoprodotti.GruppoProdottiDAO;
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
import java.util.List;

@WebServlet("/DeleteProductServlet")
public class DeleteProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public DeleteProductServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdParam = request.getParameter("productId");
        String redirectUrl = request.getContextPath() + "/adminEdit.jsp";

        if (productIdParam == null || productIdParam.trim().isEmpty()) {
            response.sendRedirect(redirectUrl + "?errorMessage=" + URLEncoder.encode("ID prodotto non fornito.", "UTF-8"));
            return;
        }

        try {
            Long productId = Long.parseLong(productIdParam);
            ProdottoDAO prodottoDAO = new ProdottoDAO();
            GruppoProdottiDAO gruppoDAO = new GruppoProdottiDAO();

            ProdottoBean prodottoDaCancellare = prodottoDAO.doRetrieveByKey(productId);

            if (prodottoDaCancellare == null) {
                response.sendRedirect(redirectUrl + "?errorMessage=" + URLEncoder.encode("Prodotto non trovato.", "UTF-8"));
                return;
            }

            Long gruppoId = prodottoDaCancellare.getGruppo();

            prodottoDAO.doDelete(productId);
            String successMessage = "Prodotto '" + prodottoDaCancellare.getNome() + "' cancellato con successo.";

            List<ProdottoBean> prodottiNelGruppoDopoCancellazione = prodottoDAO.doRetrieveByGruppo(gruppoId);

            if (prodottiNelGruppoDopoCancellazione == null || prodottiNelGruppoDopoCancellazione.isEmpty()) {
                gruppoDAO.doDelete(gruppoId);
                successMessage += " Il gruppo associato è ora vuoto e anch'esso è stato cancellato.";
            }

            if (gruppoId != null && (prodottiNelGruppoDopoCancellazione != null && !prodottiNelGruppoDopoCancellazione.isEmpty())) {
                redirectUrl += "?groupId=" + gruppoId + "&successMessage=" + URLEncoder.encode(successMessage, "UTF-8");
            } else {
                redirectUrl += "?successMessage=" + URLEncoder.encode(successMessage, "UTF-8");
            }
            response.sendRedirect(redirectUrl);

        } catch (NumberFormatException e) {
            response.sendRedirect(redirectUrl + "?errorMessage=" + URLEncoder.encode("ID prodotto non valido.", "UTF-8"));
        } catch (SQLException e) {
            System.err.println("Errore SQL nella DeleteProductServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(redirectUrl + "?errorMessage=" + URLEncoder.encode("Errore database durante la cancellazione del prodotto/gruppo. Riprova.", "UTF-8"));
        }
    }
}