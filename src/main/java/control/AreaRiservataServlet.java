package control;

import model.utente.UtenteDAO;
import model.utente.UtenteBean;
import model.infoconsegna.InfoConsegnaDAO;
import model.infoconsegna.InfoConsegnaBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/AreaRiservataServlet")
public class AreaRiservataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UtenteDAO utenteDAO;
    private InfoConsegnaDAO infoConsegnaDAO;

    public void init() throws ServletException {
        super.init();
        try {
            utenteDAO = new UtenteDAO();
            infoConsegnaDAO = new InfoConsegnaDAO();
        } catch (SQLException e) {
            System.err.println("Errore nell'inizializzazione dei DAO: " + e.getMessage());
            throw new ServletException("Errore di inizializzazione del database", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean loggedUser = (UtenteBean) session.getAttribute("user");

        try {
            UtenteBean userFromDb = utenteDAO.doRetrieveByKey(loggedUser.getEmail());

            if (userFromDb != null) {
                List<InfoConsegnaBean> infoConsegne = infoConsegnaDAO.doRetrieveByUtente(userFromDb.getEmail());
                request.setAttribute("user", userFromDb);
                request.setAttribute("infoConsegne", infoConsegne);
                request.getRequestDispatcher("/mioAccount.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL nel recupero dati utente o info consegna: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Errore nel caricamento dei dati utente.");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean loggedUser = (UtenteBean) session.getAttribute("user");

        String idConsegnaRimuoviStr = request.getParameter("idConsegnaRimuovi");
        if (idConsegnaRimuoviStr != null && !idConsegnaRimuoviStr.trim().isEmpty()) {
            try {
                Long idConsegnaRimuovi = Long.parseLong(idConsegnaRimuoviStr);
                InfoConsegnaBean infoConsegna = infoConsegnaDAO.doRetrieveByKey(idConsegnaRimuovi);

                if (infoConsegna == null || !infoConsegna.getIdUtente().equals(loggedUser.getEmail())) {
                    request.setAttribute("errorMessage", "Tentativo di rimozione indirizzo non autorizzato.");
                } else {
                    infoConsegnaDAO.doDelete(idConsegnaRimuovi);
                    request.setAttribute("successMessage", "Indirizzo di consegna rimosso con successo!");
                }
            } catch (SQLException e) {
                System.err.println("Errore SQL nella rimozione info consegna: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("errorMessage", "Errore durante la rimozione dell'indirizzo di consegna.");
            }
            doGet(request, response);
            return;
        }

        String idConsegnaStr = request.getParameter("idConsegna");
        String citta = request.getParameter("citta");
        String capStr = request.getParameter("cap");
        String via = request.getParameter("via");
        String altro = request.getParameter("altro");
        String destinatario = request.getParameter("destinatario");

        if (citta == null || citta.trim().isEmpty() ||
                capStr == null || capStr.trim().isEmpty() ||
                via == null || via.trim().isEmpty() ||
                destinatario == null || destinatario.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Città, CAP, Via e Destinatario sono obbligatori.");
            doGet(request, response);
            return;
        }

        int cap;
        try {
            cap = Integer.parseInt(capStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "CAP non valido.");
            doGet(request, response);
            return;
        }

        try {
            InfoConsegnaBean infoConsegna;

            if (idConsegnaStr != null && !idConsegnaStr.trim().isEmpty()) {
                Long idConsegna = Long.parseLong(idConsegnaStr);
                infoConsegna = infoConsegnaDAO.doRetrieveByKey(idConsegna);

                if (infoConsegna == null || !infoConsegna.getIdUtente().equals(loggedUser.getEmail())) {
                    request.setAttribute("errorMessage", "Tentativo di modifica indirizzo non autorizzato.");
                    doGet(request, response);
                    return;
                }

                infoConsegna.setCitta(citta);
                infoConsegna.setCap(cap);
                infoConsegna.setVia(via);
                infoConsegna.setAltro(altro);
                infoConsegna.setDestinatario(destinatario);

                infoConsegnaDAO.doUpdate(infoConsegna);
                request.setAttribute("successMessage", "Indirizzo di consegna aggiornato con successo!");

            } else {
                infoConsegna = new InfoConsegnaBean();
                infoConsegna.setCitta(citta);
                infoConsegna.setCap(cap);
                infoConsegna.setVia(via);
                infoConsegna.setAltro(altro);
                infoConsegna.setDestinatario(destinatario);
                infoConsegna.setIdUtente(loggedUser.getEmail());

                infoConsegnaDAO.doSave(infoConsegna);
                request.setAttribute("successMessage", "Nuovo indirizzo di consegna aggiunto con successo!");
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL nell'aggiornamento/salvataggio info consegna: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Errore durante l'aggiornamento/salvataggio dell'indirizzo di consegna.");
        }

        doGet(request, response);
    }
}
