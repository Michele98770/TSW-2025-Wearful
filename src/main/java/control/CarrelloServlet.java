package control;

import model.carrello.CarrelloBean;
import model.carrello.CarrelloDAO;
import model.cartitem.CartItemBean;
import model.cartitem.CartItemDAO;
import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;
import model.utente.UtenteBean;

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

@WebServlet("/CarrelloServlet")
public class CarrelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CarrelloDAO carrelloDAO;
    private CartItemDAO cartItemDAO;
    private ProdottoDAO prodottoDAO;

    public void init() throws ServletException {
        super.init();
        carrelloDAO = new CarrelloDAO();
        cartItemDAO = new CartItemDAO();
        prodottoDAO = new ProdottoDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean loggedUser = (UtenteBean) session.getAttribute("user");

        CarrelloBean carrelloUtente = null;
        Map<CartItemBean, ProdottoBean> cartItemsWithProducts = new LinkedHashMap<>();

        try {
            if (loggedUser != null) {
                carrelloUtente = carrelloDAO.doRetrieveByUtente(loggedUser.getEmail());
                if (carrelloUtente == null) {
                    carrelloUtente = new CarrelloBean();
                    carrelloUtente.setIdUtente(loggedUser.getEmail());
                    carrelloDAO.doSave(carrelloUtente);
                }

                List<CartItemBean> rawCartItems = cartItemDAO.doRetrieveByCarrello(carrelloUtente.getId());
                for (CartItemBean item : rawCartItems) {
                    ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(item.getProductID());
                    if (prodotto != null) {
                        cartItemsWithProducts.put(item, prodotto);
                    }
                }
            } else {
            }

            session.setAttribute("carrello", carrelloUtente); // Il CarrelloBean ID-based
            request.setAttribute("cartItemsWithProducts", cartItemsWithProducts); // La Map per la JSP

            request.getRequestDispatcher("/carrello.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Errore nel caricamento del carrello: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean loggedUser = (UtenteBean) session.getAttribute("user");
        String action = request.getParameter("action");

        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            CarrelloBean carrelloUtente = carrelloDAO.doRetrieveByUtente(loggedUser.getEmail());
            if (carrelloUtente == null) {
                carrelloUtente = new CarrelloBean();
                carrelloUtente.setIdUtente(loggedUser.getEmail());
                carrelloDAO.doSave(carrelloUtente);
            }
            session.setAttribute("carrello", carrelloUtente);

            if ("aggiungi".equals(action)) {
                Long idProdotto = Long.parseLong(request.getParameter("idProdotto"));
                int quantita = Integer.parseInt(request.getParameter("quantita"));

                ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(idProdotto);
                if (prodotto == null) {
                    request.setAttribute("message", "Prodotto non trovato!");
                    doGet(request, response);
                    return;
                }

                CartItemBean existingItem = cartItemDAO.doRetrieveByProdottoAndCarrello(idProdotto, carrelloUtente.getId());

                if (existingItem != null) {
                    existingItem.setQuantita(existingItem.getQuantita() + quantita);
                    cartItemDAO.doUpdate(existingItem);
                    request.setAttribute("message", "Quantità prodotto aggiornata nel carrello!");
                } else {
                    CartItemBean newItem = new CartItemBean(
                            idProdotto,
                            carrelloUtente.getId(),
                            quantita,
                            prodotto.isPersonalizzabile(),
                            prodotto.getImgPath()
                    );
                    cartItemDAO.doSave(newItem);
                    request.setAttribute("message", "Prodotto aggiunto al carrello!");
                }

            } else if ("rimuovi".equals(action)) {
                Long idProdotto = Long.parseLong(request.getParameter("idProdotto"));
                CartItemBean itemToRemove = cartItemDAO.doRetrieveByProdottoAndCarrello(idProdotto, carrelloUtente.getId());

                if (itemToRemove != null) {
                    cartItemDAO.doDelete(itemToRemove.getId());
                    request.setAttribute("message", "Prodotto rimosso dal carrello!");
                } else {
                    request.setAttribute("message", "Prodotto non trovato nel carrello!");
                }

            } else if ("aggiornaQuantita".equals(action)) {
                Long idProdotto = Long.parseLong(request.getParameter("idProdotto"));
                int newQuantity = Integer.parseInt(request.getParameter("quantita"));

                CartItemBean itemToUpdate = cartItemDAO.doRetrieveByProdottoAndCarrello(idProdotto, carrelloUtente.getId());
                if (itemToUpdate != null) {
                    if (newQuantity > 0) {
                        itemToUpdate.setQuantita(newQuantity);
                        cartItemDAO.doUpdate(itemToUpdate);
                        request.setAttribute("message", "Quantità aggiornata!");
                    } else {
                        cartItemDAO.doDelete(itemToUpdate.getId());
                        request.setAttribute("message", "Prodotto rimosso dal carrello (quantità a zero)!");
                    }
                } else {
                    request.setAttribute("message", "Prodotto non trovato nel carrello per l'aggiornamento!");
                }

                if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": true, \"message\": \"" + request.getAttribute("message") + "\"}");
                    return;
                }
            }

            doGet(request, response);

        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Errore durante l'operazione sul carrello: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Dati input non validi.");
            doGet(request, response);
        }
    }
}