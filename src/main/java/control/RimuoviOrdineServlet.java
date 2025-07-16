package control;

import model.ConnectionPool;
import model.ordine.OrdineDAO;
import model.orderitem.OrderItemDAO;
import model.utente.UtenteBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/RimuoviOrdineServlet")
public class RimuoviOrdineServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("user");

        if (utente == null || !utente.isAdmin()) {
            session.setAttribute("errorMessage", "Azione non autorizzata. È necessario essere un amministratore.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Long idOrdine;
        try {
            idOrdine = Long.parseLong(request.getParameter("idOrdine"));
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID ordine non valido.");
            response.sendRedirect(request.getContextPath() + "/AdminOrdini");
            return;
        }

        OrderItemDAO orderItemDAO = new OrderItemDAO();
        OrdineDAO ordineDAO = new OrdineDAO();
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);

            orderItemDAO.doDeleteByOrdine(idOrdine, connection);
            ordineDAO.doDelete(idOrdine, connection);

            connection.commit();
            session.setAttribute("successMessage", "Ordine " + idOrdine + " rimosso con successo.");

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            session.setAttribute("errorMessage", "Errore durante la rimozione dell'ordine. L'operazione è stata annullata.");
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                ConnectionPool.releaseConnection(connection);
            }
        }
        response.sendRedirect(request.getContextPath() + "/OrdiniAdmin");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}