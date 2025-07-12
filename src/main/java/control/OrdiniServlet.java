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
import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;
import model.utente.UtenteBean;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/OrdiniServlet")
public class OrdiniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private OrdineDAO ordineDAO;
    private OrderItemDAO orderItemDAO;
    private ProdottoDAO prodottoDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        ordineDAO = new OrdineDAO();
        orderItemDAO = new OrderItemDAO();
        prodottoDAO = new ProdottoDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("user");

        if (utente == null) {
            session.setAttribute("errorMessage", "Devi essere loggato per visualizzare i tuoi ordini.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<OrdineBean> ordiniUtente = new ArrayList<>();
        Map<Long, List<Map<String, Object>>> itemsPerOrdine = new LinkedHashMap<>();

        try {
            ordiniUtente = ordineDAO.doRetrieveByUtente(utente.getEmail());

            if (ordiniUtente != null && !ordiniUtente.isEmpty()) {
                for (OrdineBean ordine : ordiniUtente) {
                    List<OrderItemBean> orderItems = orderItemDAO.doRetrieveByOrdine(ordine.getId());
                    List<Map<String, Object>> currentOrderItemsWithProducts = new ArrayList<>();

                    for (OrderItemBean item : orderItems) {
                        ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(item.getIdProdotto());

                        Map<String, Object> itemWithProduct = new LinkedHashMap<>();
                        itemWithProduct.put("orderItem", item);
                        itemWithProduct.put("product", prodotto);
                        currentOrderItemsWithProducts.add(itemWithProduct);
                    }
                    itemsPerOrdine.put(ordine.getId(), currentOrderItemsWithProducts);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Si è verificato un errore durante il recupero dei tuoi ordini.");
            request.getRequestDispatcher("/errorPage.jsp").forward(request, response);
            return;
        }

        request.setAttribute("ordiniUtente", ordiniUtente);
        request.setAttribute("itemsPerOrdine", itemsPerOrdine);

        request.getRequestDispatcher("/ordini.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}