package control;

import jakarta.servlet.ServletException; // CAMBIATO
import jakarta.servlet.annotation.WebServlet; // CAMBIATO
import jakarta.servlet.http.HttpServlet; // CAMBIATO
import jakarta.servlet.http.HttpServletRequest; // CAMBIATO
import jakarta.servlet.http.HttpServletResponse; // CAMBIATO
import model.utente.UtenteDAO;
import model.utente.UtenteBean;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;
import jakarta.json.JsonValue;

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
        String errorMessage = null;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (email != null && !email.trim().isEmpty()) {
            try {
                UtenteBean existingUser = utenteDAO.doRetrieveByEmail(email);
                if (existingUser != null) {
                    isAvailable = false;
                }
            } catch (SQLException e) {
                System.err.println("Errore SQL durante il controllo email: " + e.getMessage());
                e.printStackTrace();
                isAvailable = false;
                errorMessage = "Errore del server durante la verifica dell'email. Riprova più tardi.";
            }
        } else {
            isAvailable = false;
            errorMessage = "L'email non può essere vuota.";
        }

        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("available", isAvailable)
                .add("error", (errorMessage != null) ? Json.createValue(errorMessage) : JsonValue.NULL)
                .build();

        JsonWriter jsonWriter = Json.createWriter(out);
        jsonWriter.writeObject(jsonResponse);
        jsonWriter.close();
        out.flush();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}