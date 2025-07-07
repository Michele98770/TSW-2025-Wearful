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
            List<ProdottoBean> personalizzabili = (List<ProdottoBean>) prodottoDAO.doRetrieveAll().stream().filter(e -> e.isPersonalizzabile());

            if (personalizzabili == null || personalizzabili.isEmpty()) {
                request.setAttribute("message", "Nessun prodotto personalizzabile disponibile al momento.");
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/error.jsp");
                dispatcher.forward(request, response);
                return;
            }

            Map<String, List<ProdottoBean>> groupedProducts = personalizzabili.stream()
                    .collect(Collectors.groupingBy(ProdottoBean::getNome));

            // *** INIZIO MODIFICA: Conversione con Gson in una sola riga! ***
            String jsonProducts = gson.toJson(groupedProducts);
            // *** FINE MODIFICA ***

            request.setAttribute("groupedProducts", groupedProducts);
            request.setAttribute("jsonProducts", jsonProducts);

            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/personalizzazione.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante il recupero dei prodotti dal database.");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}