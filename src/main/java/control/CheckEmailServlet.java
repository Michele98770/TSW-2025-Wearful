package control;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.utente.UtenteDAO;
import model.utente.UtenteBean;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/CheckEmailServlet")
public class CheckEmailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UtenteDAO utenteDAO;

    public void init() throws ServletException {
        super.init();
        utenteDAO = new UtenteDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        boolean isAvailable = true;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        if (email != null && !email.trim().isEmpty()) {
            try {

                UtenteBean existingUser = utenteDAO.doRetrieveByEmail(email);
                if (existingUser != null) {
                    isAvailable = false;
                }
            } catch (SQLException e) {

                System.err.println("Errore SQL durante il controllo email: " + e.getMessage());

                isAvailable = false;
            }
        } else {

            isAvailable = true;
        }


        class EmailCheckResponse {
            boolean available;
            public EmailCheckResponse(boolean available) {
                this.available = available;
            }
        }
        EmailCheckResponse res = new EmailCheckResponse(isAvailable);
        out.print(gson.toJson(res));
        out.flush();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}