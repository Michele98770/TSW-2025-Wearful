package control;

import model.ordine.OrdineBean;
import model.ordine.OrdineDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/UpdateOrderServlet")
public class UpdateOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private OrdineDAO ordineDAO;

    public UpdateOrderServlet() {
        super();
        ordineDAO = new OrdineDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idOrdineParam = request.getParameter("idOrdine");
        String nuovoStato = request.getParameter("nuovoStato");

        if (idOrdineParam == null || idOrdineParam.isEmpty() || nuovoStato == null || nuovoStato.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID Ordine o Nuovo Stato mancanti.");
            return;
        }

        try {
            Long idOrdine = Long.parseLong(idOrdineParam);
            OrdineBean ordine = ordineDAO.doRetrieveByKey(idOrdine);

            if (ordine != null) {
                if (!nuovoStato.equals("Spedito") && !nuovoStato.equals("In Consegna") && !nuovoStato.equals("Consegnato")) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Stato dell'ordine non valido.");
                    return;
                }

                ordine.setStato(nuovoStato);
                ordineDAO.doUpdate(ordine);

                response.sendRedirect(request.getContextPath() + "/OrdiniAdmin");
                return;
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ordine non trovato.");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID Ordine non valido.");
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante l'aggiornamento dell'ordine.");
            return;
        }
    }
}