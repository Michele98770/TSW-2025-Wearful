package control;

import com.google.gson.Gson;
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
    private Gson gson;

    public void init() throws ServletException {
        super.init();
        carrelloDAO = new CarrelloDAO();
        cartItemDAO = new CartItemDAO();
        prodottoDAO = new ProdottoDAO();
        gson = new Gson();
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
                    carrelloUtente = carrelloDAO.doRetrieveByUtente(loggedUser.getEmail());
                }

                List<CartItemBean> rawCartItems = cartItemDAO.doRetrieveByCarrello(carrelloUtente.getId());
                for (CartItemBean item : rawCartItems) {
                    ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(item.getProductID());
                    if (prodotto != null) {
                        cartItemsWithProducts.put(item, prodotto);
                    }
                }
            }

            session.setAttribute("carrello", carrelloUtente);
            request.setAttribute("cartItemsWithProducts", cartItemsWithProducts);

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
                carrelloUtente = carrelloDAO.doRetrieveByUtente(loggedUser.getEmail());
            }
            session.setAttribute("carrello", carrelloUtente);

            if ("aggiornaQuantita".equals(action)) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                Map<String, Object> responseMap = new LinkedHashMap<>();

                try {
                    Long idProdotto = Long.parseLong(request.getParameter("idProdotto"));
                    int newQuantity = Integer.parseInt(request.getParameter("quantita"));

                    ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(idProdotto);
                    if (prodotto == null) {
                        responseMap.put("success", false);
                        responseMap.put("message", "Prodotto non più esistente.");
                    } else if (newQuantity > prodotto.getDisponibilita()) {
                        responseMap.put("success", false);
                        responseMap.put("message", "Disponibilità non sufficiente! Solo " + prodotto.getDisponibilita() + " pezzi disponibili.");
                    } else {
                        CartItemBean itemToUpdate = cartItemDAO.doRetrieveByProdottoAndCarrello(idProdotto, carrelloUtente.getId());
                        if (itemToUpdate != null) {
                            if (newQuantity > 0) {
                                itemToUpdate.setQuantita(newQuantity);
                                cartItemDAO.doUpdate(itemToUpdate);
                                responseMap.put("success", true);
                                responseMap.put("message", "Quantità aggiornata!");
                            } else {
                                cartItemDAO.doDelete(itemToUpdate.getId());
                                responseMap.put("success", true);
                                responseMap.put("message", "Prodotto rimosso!");
                            }
                        } else {
                            responseMap.put("success", false);
                            responseMap.put("message", "Prodotto non trovato nel carrello.");
                        }
                    }
                } catch (SQLException | NumberFormatException e) {
                    responseMap.put("success", false);
                    responseMap.put("message", "Errore nei dati inviati o nel database.");
                }

                response.getWriter().write(gson.toJson(responseMap));
                return;
            }

            if ("aggiungi".equals(action)) {
                Long idProdotto = Long.parseLong(request.getParameter("idProdotto"));
                int quantita = Integer.parseInt(request.getParameter("quantita"));
                ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(idProdotto);
                if (prodotto != null) {
                    CartItemBean existingItem = cartItemDAO.doRetrieveByProdottoAndCarrello(idProdotto, carrelloUtente.getId());
                    int quantitaDaAggiungere = (existingItem != null) ? existingItem.getQuantita() + quantita : quantita;

                    if (quantitaDaAggiungere > prodotto.getDisponibilita()) {
                        session.setAttribute("errorMessage", "Disponibilità non sufficiente!");
                    } else {
                        if (existingItem != null) {
                            existingItem.setQuantita(quantitaDaAggiungere);
                            cartItemDAO.doUpdate(existingItem);
                            session.setAttribute("message", "Quantità aggiornata!");
                        } else {
                            CartItemBean newItem = new CartItemBean(idProdotto, carrelloUtente.getId(), quantita, prodotto.isPersonalizzabile(), prodotto.getImgPath());
                            cartItemDAO.doSave(newItem);
                            session.setAttribute("message", "Prodotto aggiunto!");
                        }
                    }
                } else {
                    session.setAttribute("errorMessage", "Prodotto non trovato!");
                }
            } else if ("rimuovi".equals(action)) {
                Long idProdotto = Long.parseLong(request.getParameter("idProdotto"));
                CartItemBean itemToRemove = cartItemDAO.doRetrieveByProdottoAndCarrello(idProdotto, carrelloUtente.getId());
                if (itemToRemove != null) {
                    cartItemDAO.doDelete(itemToRemove.getId());
                    session.setAttribute("message", "Prodotto rimosso!");
                }
            }

            response.sendRedirect(request.getContextPath() + "/CarrelloServlet");

        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Errore DB: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dati input non validi.");
            response.sendRedirect(request.getContextPath() + "/CarrelloServlet");
        }
    }
}