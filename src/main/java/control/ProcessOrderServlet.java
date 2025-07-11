package control;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.carrello.CarrelloBean;
import model.carrello.CarrelloDAO;
import model.cartitem.CartItemBean;
import model.cartitem.CartItemDAO;
import model.infoconsegna.InfoConsegnaBean;
import model.infoconsegna.InfoConsegnaDAO;
import model.ordine.OrdineBean;
import model.ordine.OrdineDAO;
import model.orderitem.OrderItemBean;
import model.orderitem.OrderItemDAO;
import model.prodotto.ProdottoBean;
import model.prodotto.ProdottoDAO;
import model.utente.UtenteBean;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@WebServlet("/ProcessOrderServlet")
public class ProcessOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private OrdineDAO ordineDAO;
    private OrderItemDAO orderItemDAO;
    private ProdottoDAO prodottoDAO;
    private InfoConsegnaDAO infoConsegnaDAO;
    private CartItemDAO cartItemDAO;
    private CarrelloDAO carrelloDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            ordineDAO = new OrdineDAO();
            orderItemDAO = new OrderItemDAO();
            prodottoDAO = new ProdottoDAO();
            infoConsegnaDAO = new InfoConsegnaDAO();
            cartItemDAO = new CartItemDAO();
            carrelloDAO = new CarrelloDAO();
        } catch (SQLException e) {
            throw new ServletException("Errore nell'inizializzazione dei DAO per ProcessOrderServlet", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("user");


        try {
            CarrelloBean carrelloEffettivo = carrelloDAO.doRetrieveByUtente(utente.getEmail());

            if (carrelloEffettivo == null || carrelloEffettivo.getId() == null) {
                session.setAttribute("errorMessage", "Nessun carrello trovato. Aggiungi prodotti al carrello.");
                response.sendRedirect(request.getContextPath() + "/checkout.jsp");
                return;
            }

            String selectedAddressIdStr = request.getParameter("selectedAddressId");
            String paymentMethod = request.getParameter("paymentMethod");

            if (selectedAddressIdStr == null || selectedAddressIdStr.isEmpty()) {
                session.setAttribute("errorMessage", "Seleziona un indirizzo di consegna valido.");
                response.sendRedirect(request.getContextPath() + "/checkout.jsp");
                return;
            }
            long infoConsegnaId = Long.parseLong(selectedAddressIdStr);

            InfoConsegnaBean indirizzoSelezionato = infoConsegnaDAO.doRetrieveByKey(infoConsegnaId);
            if (indirizzoSelezionato == null || !indirizzoSelezionato.getIdUtente().equals(utente.getEmail())) {
                session.setAttribute("errorMessage", "Indirizzo di consegna non valido.");
                response.sendRedirect(request.getContextPath() + "/checkout.jsp");
                return;
            }

            if ("card".equals(paymentMethod)) {
                String cardNumber = request.getParameter("cardNumber");
                String expiryDate = request.getParameter("expiryDate");
                String cvv = request.getParameter("cvv");

                if (cardNumber == null || cardNumber.isEmpty() || expiryDate == null || expiryDate.isEmpty() || cvv == null || cvv.isEmpty()) {
                    session.setAttribute("errorMessage", "Dettagli della carta incompleti.");
                    response.sendRedirect(request.getContextPath() + "/checkout.jsp");
                    return;
                }
            }

            List<CartItemBean> cartItems = cartItemDAO.doRetrieveByCarrello(carrelloEffettivo.getId());

            if (cartItems.isEmpty()) {
                session.setAttribute("errorMessage", "Il carrello è vuoto. Aggiungi prodotti prima di procedere.");
                response.sendRedirect(request.getContextPath() + "/checkout.jsp");
                return;
            }

            for (CartItemBean cartItem : cartItems) {
                ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(cartItem.getProductID());
                if (prodotto == null || prodotto.getDisponibilita() < cartItem.getQuantita()) {
                    session.setAttribute("errorMessage", "Disponibilità insufficiente per il prodotto: " + (prodotto != null ? prodotto.getNome() : "ID Prodotto: " + cartItem.getProductID()));
                    response.sendRedirect(request.getContextPath() + "/checkout.jsp");
                    return;
                }
            }

            OrdineBean nuovoOrdine = new OrdineBean();
            nuovoOrdine.setIdUtente(utente.getEmail());
            nuovoOrdine.setInfoConsegna(infoConsegnaId);
            nuovoOrdine.setDataOrdine(new Date());
            ordineDAO.doSave(nuovoOrdine);

            for (CartItemBean cartItem : cartItems) {
                ProdottoBean prodotto = prodottoDAO.doRetrieveByKey(cartItem.getProductID());

                OrderItemBean orderItem = new OrderItemBean();
                orderItem.setIdOrdine(nuovoOrdine.getId());
                orderItem.setIdProdotto(prodotto.getId());
                orderItem.setNome(prodotto.getNome());

                if(cartItem.isPersonalizzato()){
                    orderItem.setPrezzo(prodotto.getPrezzo()+(prodotto.getPrezzo()/100)*20);
                }else{
                    orderItem.setPrezzo(prodotto.getPrezzo());
                }

                orderItem.setQuantita(cartItem.getQuantita());
                orderItem.setIva(prodotto.getIva());
                orderItemDAO.doSave(orderItem);

                prodottoDAO.updateDisponibilita(prodotto.getId(), -cartItem.getQuantita());

                cartItemDAO.doDelete(cartItem.getId());
            }

            carrelloDAO.doDelete(carrelloEffettivo.getId());

            session.removeAttribute("carrello");
            response.sendRedirect(request.getContextPath() + "/ordineEffettuato.jsp");

        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Si è verificato un errore critico durante l'elaborazione dell'ordine. " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/checkout.jsp");
        }
    }
}