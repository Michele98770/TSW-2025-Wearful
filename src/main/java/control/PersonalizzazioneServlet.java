package control;

import com.google.gson.Gson;
import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/PersonalizzazioneServlet")
public class PersonalizzazioneServlet extends HttpServlet {
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProdottoDAO prodottoDAO = new ProdottoDAO();
        try {
            List<ProdottoBean> allPersonalizableVariants = prodottoDAO.doRetrieveByPersonalizzabile(true);


            Map<String, List<ProdottoBean>> groupedByName = allPersonalizableVariants.stream()
                    .collect(Collectors.groupingBy(ProdottoBean::getNome));

            Map<String, Map<String, Map<String, ProdottoBean>>> fullProductData = new HashMap<>();
            groupedByName.forEach((groupName, variants) -> {
                Map<String, Map<String, ProdottoBean>> byColor = new HashMap<>();
                for (ProdottoBean variant : variants) {
                    byColor.computeIfAbsent(variant.getColore(), k -> new HashMap<>())
                            .put(variant.getTaglia(), variant);
                }
                fullProductData.put(groupName, byColor);
            });

            List<ProdottoBean> representativeProducts = new ArrayList<>();
            for(List<ProdottoBean> group : groupedByName.values()) {
                representativeProducts.add(group.get(0));
            }

            String jsonProducts = gson.toJson(fullProductData);

            request.setAttribute("representativeProducts", representativeProducts);
            request.setAttribute("jsonProductsData", jsonProducts);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/personalizzaMaglia.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore SQL nel caricamento dei prodotti.");
        }
    }
}