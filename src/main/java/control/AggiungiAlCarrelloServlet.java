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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@WebServlet("/AggiungiAlCarrelloServlet")
public class AggiungiAlCarrelloServlet extends HttpServlet {
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean loggedUser = (UtenteBean) session.getAttribute("user");

        Long idProdotto = null;
        int quantita = 0;
        String message = null;

        try {
            idProdotto = Long.parseLong(request.getParameter("idProdotto"));
            quantita = Integer.parseInt(request.getParameter("quantita"));
            if (quantita <= 0) {
                request.setAttribute("errorMessage", "La quantità deve essere almeno 1.");
                request.getRequestDispatcher("/prodotto.jsp?id=" + idProdotto).forward(request, response);
                return;
            }

            ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(idProdotto);
            if (prodotto == null) {
                request.setAttribute("errorMessage", "Prodotto non trovato.");
                request.getRequestDispatcher("/prodotto.jsp?id=" + idProdotto).forward(request, response);
                return;
            }

            if (loggedUser != null) {
                CarrelloBean carrelloUtente = carrelloDAO.doRetrieveByUtente(loggedUser.getEmail());
                if (carrelloUtente == null) {
                    carrelloUtente = new CarrelloBean();
                    carrelloUtente.setIdUtente(loggedUser.getEmail());
                    carrelloDAO.doSave(carrelloUtente);
                    session.setAttribute("carrello", carrelloUtente);
                }

                CartItemBean existingItem = cartItemDAO.doRetrieveByProdottoAndCarrello(idProdotto, carrelloUtente.getId());

                if (existingItem != null) {
                    existingItem.setQuantita(existingItem.getQuantita() + quantita);
                    cartItemDAO.doUpdate(existingItem);
                    message = "Quantità aggiornata nel carrello!";
                } else {
                    CartItemBean newItem = new CartItemBean(
                            idProdotto,
                            carrelloUtente.getId(),
                            quantita,
                            prodotto.isPersonalizzabile(),
                            prodotto.getImgPath()
                    );
                    cartItemDAO.doSave(newItem);
                    message = "Prodotto aggiunto al carrello!";
                }
                session.setAttribute("cartCount", CarrelloServlet.getCartItemCount(request, loggedUser.getEmail()));
            } else {
                Map<Long, Integer> guestCart = getGuestCartFromCookies(request);

                if (guestCart.containsKey(idProdotto)) {
                    guestCart.put(idProdotto, guestCart.get(idProdotto) + quantita);
                    message = "Quantità aggiornata nel carrello guest!";
                } else {
                    guestCart.put(idProdotto, quantita);
                    message = "Prodotto aggiunto al carrello guest!";
                }
                saveGuestCartToCookies(response, guestCart);
                session.setAttribute("cartCount", CarrelloServlet.getCartItemCount(request, null));
            }

            request.setAttribute("message", message);
            response.sendRedirect(request.getContextPath() + "/aggiuntaCarrello.jsp?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8.toString()));

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Quantità o ID prodotto non validi.");
            request.getRequestDispatcher("/prodotto.jsp?id=" + (idProdotto != null ? idProdotto : "")).forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Errore del database durante l'aggiunta al carrello: " + e.getMessage());
            request.getRequestDispatcher("/prodotto.jsp?id=" + (idProdotto != null ? idProdotto : "")).forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Si è verificato un errore inaspettato: " + e.getMessage());
            request.getRequestDispatcher("/prodotto.jsp?id=" + (idProdotto != null ? idProdotto : "")).forward(request, response);
        }
    }

    private Map<Long, Integer> getGuestCartFromCookies(HttpServletRequest request) {
        Map<Long, Integer> guestCart = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("guest_cart".equals(cookie.getName())) {
                    try {
                        String cartJsonString = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                        Type type = new TypeToken<Map<Long, Integer>>() {}.getType();
                        guestCart = gson.fromJson(cartJsonString, type);
                        if (guestCart == null) {
                            guestCart = new HashMap<>();
                        }
                    } catch (Exception ignored) {
                    }
                    break;
                }
            }
        }
        return guestCart;
    }

    private void saveGuestCartToCookies(HttpServletResponse response, Map<Long, Integer> guestCart) {
        String cartJsonString = gson.toJson(guestCart);

        Cookie cookie = new Cookie("guest_cart", URLEncoder.encode(cartJsonString, StandardCharsets.UTF_8));
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}