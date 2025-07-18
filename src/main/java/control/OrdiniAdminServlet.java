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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date; // Importa java.util.Date
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
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        String startDateParam = request.getParameter("startDate");
        String endDateParam = request.getParameter("endDate");

        Date startDate = null;
        Date endDate = null;

        try {
            if (startDateParam != null && !startDateParam.trim().isEmpty()) {
                startDate = sdf.parse(startDateParam);
            }
            if (endDateParam != null && !endDateParam.trim().isEmpty()) {
                endDate = sdf.parse(endDateParam);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            startDate = null;
            endDate = null;
        }

        try {
            List<OrdineBean> tuttiGliOrdini;

            if ("totale".equals(sortBy)) {
                tuttiGliOrdini = ordineDAO.doRetrieveAll(startDate, endDate);
                Map<Long, Double> totaliOrdini = new LinkedHashMap<>();
                for (OrdineBean ordine : tuttiGliOrdini) {
                    List<OrderItemBean> items = orderItemDAO.doRetrieveByOrdine(ordine.getId());
                    double currentTotale = 0.0;
                    for (OrderItemBean item : items) {
                        currentTotale += item.getTotaleConIva();
                    }
                    totaliOrdini.put(ordine.getId(), currentTotale);
                }

                if ("asc".equals(sortOrder)) {
                    tuttiGliOrdini.sort(Comparator.comparing(o -> totaliOrdini.getOrDefault(o.getId(), 0.0)));
                } else {
                    tuttiGliOrdini.sort(Comparator.comparing((OrdineBean o) -> totaliOrdini.getOrDefault(o.getId(), 0.0)).reversed());
                }
            } else {
                if (sortBy == null || sortBy.isEmpty()) {
                    sortBy = "dataOrdine";
                }
                if (sortOrder == null || sortOrder.isEmpty()) {
                    sortOrder = "desc";
                }
                tuttiGliOrdini = ordineDAO.doRetrieveAll(sortBy, sortOrder, startDate, endDate);
            }

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
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("sortOrder", sortOrder);
            request.setAttribute("startDateFilter", startDateParam);
            request.setAttribute("endDateFilter", endDateParam);

            String url = "./ordiniAdmin.jsp";
            RequestDispatcher dispatcher = request.getRequestDispatcher(url);
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/500.jsp");
        }
    }
}