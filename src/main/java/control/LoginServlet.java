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
import control.Security;

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
            utenteLoggato = utenteDAO.doLogin(email, Security.hashPassword(password));
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Errore del server durante il login. Riprova più tardi.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        if (utenteLoggato != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", utenteLoggato);
            session.setMaxInactiveInterval(30 * 60);

            session.setAttribute("welcomeMessageUsername", utenteLoggato.getUsername());

            if(utenteLoggato.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/adminUpload.jsp");
            } else {
                response.sendRedirect("CatalogoServlet");
            }
        } else {
            request.setAttribute("errorMessage", "Email o password non validi.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}