package control;

import model.utente.UtenteBean;
import model.utente.UtenteDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/CheckEmailServlet")
public class CheckEmailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            if (email == null || email.trim().isEmpty()) {

                out.print("{\"error\": \"Parametro email mancante o vuoto\"}");
                return;
            }

            boolean emailDisponibile = false;
            UtenteDAO dao = new UtenteDAO();
            UtenteBean user= dao.doRetrieveByKey(email);
            if(user==null){
                emailDisponibile = true;
            }

            out.print("{\"available\": " + emailDisponibile + "}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Errore interno del server\"}");
        } finally {
            out.flush();
            out.close();
        }
    }
}
