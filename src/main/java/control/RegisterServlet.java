package control;

import model.utente.UtenteBean;
import model.utente.UtenteDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\+\\d{1,13}$");

    public RegisterServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> errorMessages = new ArrayList<>();

        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String telefono = request.getParameter("telefono");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");

        if (email == null || email.trim().isEmpty()) {
            errorMessages.add("L'email è obbligatoria.");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errorMessages.add("Formato email non valido.");
        }

        if (username == null || username.trim().isEmpty()) {
            errorMessages.add("Lo username è obbligatorio.");
        } else if (username.length() > 50) {
            errorMessages.add("Lo username non può superare i 50 caratteri.");
        }

        if (telefono == null || telefono.trim().isEmpty()) {
            errorMessages.add("Il telefono è obbligatorio.");
        } else if (!TELEFONO_PATTERN.matcher(telefono).matches()) {
            errorMessages.add("Il numero di telefono deve contenere il prefisso e 10 cifre");
        }

        if (password == null || password.trim().isEmpty()) {
            errorMessages.add("La password è obbligatoria.");
        } else if (password.length() < 8) {
            errorMessages.add("La password deve essere di almeno 8 caratteri.");
        } else if (!password.equals(confirmPassword)) {
            errorMessages.add("Le password non corrispondono.");
        }

        UtenteDAO utenteDAO = null;
        utenteDAO = new UtenteDAO();

        if (errorMessages.isEmpty()) {
            try {
                if (utenteDAO.doRetrieveByKey(email) != null) {
                    errorMessages.add("Questa email è già registrata.");
                }
            } catch (SQLException e) {
                System.err.println("Errore SQL durante il controllo unicità email: " + e.getMessage());
                errorMessages.add("Errore interno del server. Riprova più tardi.");
            }
        }

        if (!errorMessages.isEmpty()) {
            request.setAttribute("errorMessage", String.join("<br>", errorMessages));
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        try {
            UtenteBean nuovoUtente = new UtenteBean();
            nuovoUtente.setEmail(email);
            nuovoUtente.setUsername(username);
            nuovoUtente.setTelefono(telefono);

            String hashedPassword = hashPasswordSHA256(password);
            nuovoUtente.setPassword(hashedPassword);

            nuovoUtente.setAdmin(false);

            utenteDAO.doSave(nuovoUtente);
            String success= "Registrazione avvenuta con successo!";
            response.sendRedirect(request.getContextPath() + "/LoginServlet?email=" + email +"&success="+success);


        } catch (SQLException e) {
            System.err.println("Errore SQL durante la registrazione utente: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Si è verificato un errore durante la registrazione. Riprova più tardi.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }

    private String hashPasswordSHA256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Errore: Algoritmo SHA-256 non trovato. " + e.getMessage());
            throw new RuntimeException("Errore interno durante l'hashing della password.", e);
        }
    }
}