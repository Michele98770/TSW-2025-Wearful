package control;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import model.utente.UtenteBean;
import model.utente.UtenteDAO;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email e password non possono essere vuoti.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        UtenteDAO utenteDAO = new UtenteDAO();
        UtenteBean utenteLoggato = null;

        try {
            // Utilizza il metodo doLogin del tuo DAO per recuperare l'utente
            utenteLoggato = utenteDAO.doLogin(email, Security.hashPassword(password));
        } catch (SQLException e) {
            e.printStackTrace(); // Stampa l'errore per debug
            request.setAttribute("errorMessage", "Errore del server durante il login. Riprova più tardi.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        if (utenteLoggato != null) {

            HttpSession session = request.getSession(); // Ottieni o crea la sessione
            session.setAttribute("user", utenteLoggato);
            session.setAttribute("currentUser",utenteLoggato.getEmail());
            session.setMaxInactiveInterval(30 * 60);
            if(utenteLoggato.isAdmin()) {
                session.setAttribute("isAdmin", true);
                response.sendRedirect(request.getContextPath() + "/adminUpload.jsp");
            }
            else
                response.sendRedirect(request.getContextPath()+"/loginSuccess.jsp");

        } else {
            request.setAttribute("errorMessage", "Email o password non validi.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}