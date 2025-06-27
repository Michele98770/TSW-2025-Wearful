package control;

import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet("/CatalogoServlet")
public class CatalogoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ProdottoDAO prodottoDAO;
    private static final int ITEMS_PER_PAGE = 8;

    public void init() throws ServletException {
        super.init();
        prodottoDAO = new ProdottoDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "filter";
        }

        String category = null;
        Float minPrice = null;
        Float maxPrice = null;
        List<String> sizes = null;
        String sortBy = null;
        int currentPage = 1;

        if ("filter".equals(action)) {
            category = request.getParameter("category");
            if (category != null && category.isEmpty()) category = null;

            String minPriceStr = request.getParameter("minPrice");
            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                try {
                    minPrice = Float.parseFloat(minPriceStr);
                } catch (NumberFormatException e) {
                    minPrice = null;
                }
            }

            String maxPriceStr = request.getParameter("maxPrice");
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                try {
                    maxPrice = Float.parseFloat(maxPriceStr);
                } catch (NumberFormatException e) {
                    maxPrice = null;
                }
            }

            String[] sizeArray = request.getParameterValues("size");
            if (sizeArray != null && sizeArray.length > 0) {
                sizes = Arrays.asList(sizeArray);
            } else {
                sizes = new ArrayList<>();
            }

            sortBy = request.getParameter("sortBy");

            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.isEmpty()) {
                try {
                    currentPage = Integer.parseInt(pageStr);
                } catch (NumberFormatException e) {
                    currentPage = 1;
                }
            }
        }

        List<ProdottoBean> products = new ArrayList<>();
        int totalProducts = 0;
        int totalPages = 1;

        try {
            // Recupera tutti i prodotti filtrati, prima di applicare la logica di "distinct per gruppo"
            List<ProdottoBean> allFilteredProducts = prodottoDAO.doRetrieveAllFiltered(category, minPrice, maxPrice, sizes, 0, Integer.MAX_VALUE, sortBy); // Recupera potenzialmente tutti i prodotti per calcolare il distinct

            // Filtra i prodotti per mostrare solo un'istanza per 'gruppo'
            List<ProdottoBean> distinctProductsByGroup = new ArrayList<>();
            Set<Long> seenGroups = new HashSet<>();
            for (ProdottoBean product : allFilteredProducts) {
                if (!seenGroups.contains(product.getGruppo())) {
                    distinctProductsByGroup.add(product);
                    seenGroups.add(product.getGruppo());
                }
            }

            totalProducts = distinctProductsByGroup.size();

            totalPages = (int) Math.ceil((double) totalProducts / ITEMS_PER_PAGE);
            if (totalPages == 0) totalPages = 1;

            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages) currentPage = totalPages;

            int offset = (currentPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(offset + ITEMS_PER_PAGE, totalProducts);

            // Estrai la sotto-lista per la paginazione dopo aver applicato il distinct
            if (offset < totalProducts) {
                products = distinctProductsByGroup.subList(offset, endIndex);
            } else {
                products = new ArrayList<>(); // Nessun prodotto per questa pagina
            }

        } catch (SQLException e) {
            System.err.println("Errore SQL nella CatalogoServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Errore nel caricamento dei prodotti dal database.");
        }

        request.setAttribute("products", products);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("filterCategory", category);
        request.setAttribute("filterMinPrice", minPrice != null ? String.valueOf(minPrice) : "");
        request.setAttribute("filterMaxPrice", maxPrice != null ? String.valueOf(maxPrice) : "");
        request.setAttribute("filterSizes", sizes);
        request.setAttribute("filterSortBy", sortBy);

        request.getRequestDispatcher("/catalogo.jsp").forward(request, response);
    }
}