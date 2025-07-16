package control;

import model.ordine.OrdineBean;
import model.ordine.OrdineDAO;
import model.orderitem.OrderItemBean;
import model.orderitem.OrderItemDAO;
import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;
import model.utente.UtenteBean;
import model.utente.UtenteDAO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/OrdiniAdmin")
public class OrdiniAdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final OrdineDAO ordineDAO = new OrdineDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final ProdottoDAO prodottoDAO = new ProdottoDAO();
    private final UtenteDAO utenteDAO = new UtenteDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();


        try {
            List<OrdineBean> tuttiGliOrdini = ordineDAO.doRetrieveAll();

            tuttiGliOrdini.sort(Comparator.comparing(OrdineBean::getDataOrdine).reversed());

            Map<Long, List<Map<String, Object>>> itemsPerOrdine = new LinkedHashMap<>();
            Map<Long, UtenteBean> utentiPerOrdine = new HashMap<>();

            if (tuttiGliOrdini != null) {
                for (OrdineBean ordine : tuttiGliOrdini) {
                    long idOrdine = ordine.getId();

                    List<OrderItemBean> items = orderItemDAO.doRetrieveByOrdine(idOrdine);
                    List<Map<String, Object>> itemsConProdotti = new ArrayList<>();

                    for (OrderItemBean item : items) {
                        ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(item.getIdProdotto());
                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("orderItem", item);
                        itemMap.put("product", prodotto);
                        itemsConProdotti.add(itemMap);
                    }
                    itemsPerOrdine.put(idOrdine, itemsConProdotti);

                    UtenteBean cliente = utenteDAO.doRetrieveByKey(ordine.getIdUtente());
                    utentiPerOrdine.put(idOrdine, cliente);
                }
            }

            request.setAttribute("tuttiGliOrdini", tuttiGliOrdini);
            request.setAttribute("itemsPerOrdine", itemsPerOrdine);
            request.setAttribute("utentiPerOrdine", utentiPerOrdine);

            String url = "/ordiniAdmin.jsp";
            RequestDispatcher dispatcher = request.getRequestDispatcher(url);
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/500.jsp");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}