package control;

import model.carrello.CarrelloBean;
import model.carrello.CarrelloDAO;
import model.cartitem.CartItemBean;
import model.cartitem.CartItemDAO;
import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;
import model.utente.UtenteBean;
import model.infoconsegna.InfoConsegnaBean;
import model.infoconsegna.InfoConsegnaDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/CheckoutServlet")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private CarrelloDAO carrelloDAO;
    private CartItemDAO cartItemDAO;
    private ProdottoDAO prodottoDAO;
    private InfoConsegnaDAO infoConsegnaDAO;

    public void init() throws ServletException {
        super.init();
        carrelloDAO = new CarrelloDAO();
        cartItemDAO = new CartItemDAO();
        prodottoDAO = new ProdottoDAO();
        try {
            infoConsegnaDAO = new InfoConsegnaDAO();
        } catch (SQLException e) {
            throw new ServletException("Errore durante l'inizializzazione di InfoConsegnaDAO", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean loggedUser = (UtenteBean) session.getAttribute("user");

        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet?error=Accedi per completare l'ordine.");
            return;
        }

        try {
            CarrelloBean carrelloUtente = carrelloDAO.doRetrieveByUtente(loggedUser.getEmail());

            if (carrelloUtente == null) {
                request.setAttribute("errorMessage", "Il tuo carrello è vuoto. Aggiungi prodotti prima di procedere.");
                request.getRequestDispatcher("/carrello.jsp").forward(request, response);
                return;
            }

            List<CartItemBean> cartItems = cartItemDAO.doRetrieveByCarrello(carrelloUtente.getId());

            if (cartItems.isEmpty()) {
                request.setAttribute("errorMessage", "Il tuo carrello è vuoto. Aggiungi prodotti prima di procedere.");
                request.getRequestDispatcher("/carrello.jsp").forward(request, response);
                return;
            }

            Map<CartItemBean, ProdottoBean> cartItemsWithProducts = new LinkedHashMap<>();
            double totaleCarrello = 0.0;

            for (CartItemBean item : cartItems) {
                ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(item.getProductID());
                if (prodotto != null) {
                    cartItemsWithProducts.put(item, prodotto);
                    double subtotaleItem;
                    if (!item.isPersonalizzato()) {
                        subtotaleItem = prodotto.getPrezzoFinale() * item.getQuantita();
                    } else {
                        subtotaleItem = (prodotto.getPrezzoFinale() + (prodotto.getPrezzoFinale() / 100.0 * 20)) * item.getQuantita();
                    }
                    totaleCarrello += subtotaleItem;
                }
            }

            List<InfoConsegnaBean> infoConsegnaUtente = infoConsegnaDAO.doRetrieveByUtente(loggedUser.getEmail());

            request.setAttribute("cartItemsWithProducts", cartItemsWithProducts);
            request.setAttribute("totaleCarrello", totaleCarrello);
            request.setAttribute("infoConsegnaUtente", infoConsegnaUtente);

            request.getRequestDispatcher("/checkout.jsp").forward(request, response); // Modificato il path della JSP

        } catch (SQLException e) {
            System.err.println("Errore SQL nella CheckoutServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Errore del database durante la preparazione dell'ordine. Riprova.");
            request.getRequestDispatcher("/carrello.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Errore generico nella CheckoutServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Si è verificato un errore inaspettato. Riprova.");
            request.getRequestDispatcher("/carrello.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}