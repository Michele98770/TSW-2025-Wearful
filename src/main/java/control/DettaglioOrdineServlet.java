package control;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.ordine.OrdineBean;
import model.ordine.OrdineDAO;
import model.orderitem.OrderItemBean;
import model.orderitem.OrderItemDAO;
import model.infoconsegna.InfoConsegnaBean;
import model.infoconsegna.InfoConsegnaDAO;
import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;
import model.utente.UtenteBean;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/DettaglioOrdineServlet")
public class DettaglioOrdineServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private OrdineDAO ordineDAO;
    private OrderItemDAO orderItemDAO;
    private InfoConsegnaDAO infoConsegnaDAO;
    private ProdottoDAO prodottoDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            ordineDAO = new OrdineDAO();
            orderItemDAO = new OrderItemDAO();
            infoConsegnaDAO = new InfoConsegnaDAO();
            prodottoDAO = new ProdottoDAO();
        } catch (SQLException e) {
            throw new ServletException("Errore nell'inizializzazione dei DAO per DettaglioOrdineServlet", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("user");

        if (utente == null) {
            session.setAttribute("errorMessage", "Devi essere loggato per visualizzare i dettagli dell'ordine.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Long idOrdine = null;
        try {
            idOrdine = Long.parseLong(request.getParameter("idOrdine"));
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID ordine non valido.");
            response.sendRedirect(request.getContextPath() + "/OrdiniServlet");
            return;
        }

        OrdineBean ordine = null;
        List<OrderItemBean> orderItems = new ArrayList<>();
        InfoConsegnaBean infoConsegna = null;
        Map<Long, ProdottoBean> prodottiMappa = new LinkedHashMap<>();

        try {
            ordine = ordineDAO.doRetrieveByKey(idOrdine);

            if (ordine == null || (!ordine.getIdUtente().equals(utente.getEmail()) && !utente.isAdmin())) {
                session.setAttribute("errorMessage", "Ordine non trovato o non sei autorizzato a visualizzarlo.");
                response.sendRedirect(request.getContextPath() + "/OrdiniServlet");
                return;
            }

            orderItems = orderItemDAO.doRetrieveByOrdine(idOrdine);
            infoConsegna = infoConsegnaDAO.doRetrieveByKey(ordine.getInfoConsegna());

            if (orderItems != null && !orderItems.isEmpty()) {
                for (OrderItemBean item : orderItems) {
                    ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(item.getIdProdotto());
                    if (prodotto != null) {
                        prodottiMappa.put(prodotto.getId(), prodotto);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Si è verificato un errore durante il recupero dei dettagli dell'ordine.");
            response.sendRedirect(request.getContextPath() + "/OrdiniServlet");
            return;
        }

        request.setAttribute("ordine", ordine);
        request.setAttribute("orderItems", orderItems);
        request.setAttribute("infoConsegna", infoConsegna);
        request.setAttribute("prodottiMappa", prodottiMappa);

        request.getRequestDispatcher("/dettaglioOrdine.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}