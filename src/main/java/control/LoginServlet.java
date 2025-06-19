package control;

import model.utente.UtenteBean;
import model.utente.UtenteDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public LoginServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UtenteDAO utenteDAO = new UtenteDAO();
        UtenteBean utente = null;

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Per favore, inserisci email e password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        try {
            utente = utenteDAO.doRetrieveByKey(email);

            if (utente != null) {
                String hashedPasswordInput = hashPasswordSHA256(password);

                if (hashedPasswordInput.equals(utente.getPassword())) {
                    HttpSession session = request.getSession();
                    session.setAttribute("currentUser", utente);

                    if (utente.isAdmin()) {
                        request.setAttribute("loginStatus", "Login admin effettuato");
                        request.getRequestDispatcher("login_success.jsp").forward(request, response);
                    } else {
                        request.setAttribute("loginStatus", "Login utente effettuato");
                        request.getRequestDispatcher("login_success.jsp").forward(request, response);
                    }
                } else {
                    request.setAttribute("errorMessage", "Credenziali errate.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("errorMessage", "Credenziali errate.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Errore SQL durante il login: " + e.getMessage());
            request.setAttribute("errorMessage", "Si è verificato un errore interno. Riprova più tardi.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.err.println("Errore: Algoritmo di hashing non trovato. " + e.getMessage());
            request.setAttribute("errorMessage", "Si è verificato un errore interno del server.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    private String hashPasswordSHA256(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}