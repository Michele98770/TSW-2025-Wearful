package control;

import javax.servlet.RequestDispatcher;
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
import control.CarrelloServlet;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String error= (String)request.getParameter("error");
        String success= (String)request.getParameter("success");

        request.setAttribute("errorMessage", error);
        request.setAttribute("successMessage", success);

        RequestDispatcher rd= request.getRequestDispatcher("login.jsp");
        rd.forward(request,response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

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

            try {
                CarrelloServlet.mergeGuestCartToUserCart(request, response, utenteLoggato.getEmail());
            } catch (SQLException e) {
                System.err.println("Errore durante la fusione del carrello guest per l'utente " + utenteLoggato.getEmail() + ": " + e.getMessage());
                e.printStackTrace();
            }

            response.sendRedirect(request.getContextPath() + "/CatalogoServlet");

            try {
                session.setAttribute("cartCount", CarrelloServlet.getCartItemCount(request, utenteLoggato.getEmail()));
            } catch (SQLException e) {
                System.err.println("Errore nel recupero del cartCount all'avvio: " + e.getMessage());
                session.setAttribute("cartCount", 0);
            }
        } else {
            request.setAttribute("errorMessage", "Email o password non validi.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}