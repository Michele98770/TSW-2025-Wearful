package control;

import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;
import model.utente.UtenteBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



@WebServlet("/CatalogoServlet")
public class CatalogoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ProdottoDAO prodottoDAO;
    private static final int ITEMS_PER_PAGE = 9;

    public void init() throws ServletException {
        super.init();
        prodottoDAO = new ProdottoDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session= request.getSession();
        UtenteBean loggedUser= (UtenteBean) session.getAttribute("user");

        try {
            session.setAttribute("cartCount", CarrelloServlet.getCartItemCount(request, loggedUser != null ? loggedUser.getEmail() : null));
        } catch (SQLException e) {
            System.err.println("Errore nel recupero del cartCount all'avvio: " + e.getMessage());
            session.setAttribute("cartCount", 0);
        }

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "filter";
        }

        String searchQuery = request.getParameter("searchQuery");

        if (searchQuery != null && searchQuery.isEmpty()) searchQuery = null;

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

            totalProducts = prodottoDAO.countAllFiltered(category, minPrice, maxPrice, sizes, searchQuery);

            totalPages = (int) Math.ceil((double) totalProducts / ITEMS_PER_PAGE);
            if (totalPages == 0) totalPages = 1;

            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages) currentPage = totalPages;

            int offset = (currentPage - 1) * ITEMS_PER_PAGE;

            products = prodottoDAO.doRetrieveAllFiltered(category, minPrice, maxPrice, sizes, offset, ITEMS_PER_PAGE, sortBy, searchQuery);


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
        request.setAttribute("searchQuery", searchQuery);

        request.getRequestDispatcher("/catalogo.jsp").forward(request, response);
    }
}