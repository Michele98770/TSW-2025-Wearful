<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.carrello.CarrelloBean" %>
<%@ page import="model.cartitem.CartItemBean" %>
<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="model.infoconsegna.InfoConsegnaBean" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="model.utente.UtenteBean" %>

<%
  Map<CartItemBean, ProdottoBean> cartItemsWithProducts = (Map<CartItemBean, ProdottoBean>) request.getAttribute("cartItemsWithProducts");
  Double totaleCarrello = (Double) request.getAttribute("totaleCarrello");
  List<InfoConsegnaBean> infoConsegnaUtente = (List<InfoConsegnaBean>) request.getAttribute("infoConsegnaUtente");

  if (cartItemsWithProducts == null) {
    cartItemsWithProducts = new LinkedHashMap<>();
  }
  if (totaleCarrello == null) {
    totaleCarrello = 0.0;
  }
  if (infoConsegnaUtente == null) {
    infoConsegnaUtente = new java.util.ArrayList<>();
  }

  String errorMessage = (String) request.getAttribute("errorMessage");
  if (errorMessage == null) {
    errorMessage = (String) session.getAttribute("errorMessage");
    session.removeAttribute("errorMessage");
  }
%>

<!DOCTYPE html>
<html lang="it">
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/small_logo.png">
  <meta charset="UTF-8">
  <title>Conferma Ordine</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/checkout.css?v=1.0">
</head>
<body class="page-checkout">

<jsp:include page="header.jsp" />

<main>
  <div class="checkout-page">
    <h2>Riepilogo e Conferma Ordine</h2>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="error-message"><%= errorMessage %></div>
    <% } %>

    <form action="ProcessOrderServlet" method="post" id="checkoutForm" novalidate>

      <div class="section">
        <h3>Totale da Pagare:</h3>
        <div class="total-summary">
          <span class="total-amount">€ <%= String.format("%.2f", totaleCarrello) %></span>
        </div>
      </div>

      <div class="section">
        <h3>Scegli Indirizzo di Consegna:</h3>
        <% if (infoConsegnaUtente.isEmpty()) { %>
        <p class="info-message">Nessun indirizzo di consegna salvato. aggiungili nella tua area personale</p>
        <a href="AreaRiservataServlet" class="btn btn-secondary">Area Personale</a>
        <% } else { %>
        <div class="address-selection-grid">
          <% for (int i = 0; i < infoConsegnaUtente.size(); i++) {
            InfoConsegnaBean infoConsegna = infoConsegnaUtente.get(i); %>
          <div class="address-card">
            <input type="radio" id="address_<%= infoConsegna.getId() %>" name="selectedAddressId" value="<%= infoConsegna.getId() %>" <%= (i == 0) ? "checked" : "" %>>
            <label for="address_<%= infoConsegna.getId() %>">
              <strong><%= infoConsegna.getDestinatario() %></strong><br>
              <%= infoConsegna.getVia() %> <%= infoConsegna.getAltro() %><br>
              <%= infoConsegna.getCap() %> <%= infoConsegna.getCitta() %><br>
            </label>
          </div>
          <% } %>
        </div>
        <% } %>
      </div>

      <div class="section">
        <h3>Scegli Metodo di Pagamento:</h3>
        <div class="payment-methods">
          <input type="radio" id="payment_cash" name="paymentMethod" value="cash" checked>
          <label for="payment_cash">Contanti alla consegna</label>

          <input type="radio" id="payment_card" name="paymentMethod" value="card">
          <label for="payment_card">Paga con Carta</label>
        </div>

        <div id="cardDetails" class="card-details-group">
          <h4>Dettagli Carta</h4>
          <label for="cardNumber">Numero Carta:</label>
          <input type="text" id="cardNumber" name="cardNumber" placeholder="XXXX XXXX XXXX XXXX" maxlength="16">

          <label for="expiryDate">Data Scadenza:</label>
          <input type="text" id="expiryDate" name="expiryDate" placeholder="MM/YY" maxlength="5">

          <label for="cvv">CVV:</label>
          <input type="text" id="cvv" name="cvv" placeholder="XXX" maxlength="3">
        </div>
      </div>

      <div class="section">
        <button type="submit" class="confirm-order-btn" id="confirmOrderBtn" <%= infoConsegnaUtente.isEmpty() ? "disabled" : "" %>>Conferma Ordine</button>
      </div>
    </form>
  </div>
</main>

<jsp:include page="footer.jsp" />
<script src="<%= request.getContextPath() %>/scripts/checkout.js"></script>
</body>
</html>