<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.carrello.CarrelloBean" %>
<%@ page import="model.cartitem.CartItemBean" %>
<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="model.utente.UtenteBean" %>

<%
  CarrelloBean carrelloUtente = (CarrelloBean) session.getAttribute("carrello");
  Map<CartItemBean, ProdottoBean> cartItemsWithProducts = (Map<CartItemBean, ProdottoBean>) request.getAttribute("cartItemsWithProducts");

  if (cartItemsWithProducts == null) {
    cartItemsWithProducts = new LinkedHashMap<>();
  }

  String message = (String) session.getAttribute("message");
  String errorMessage = (String) session.getAttribute("errorMessage");
  session.removeAttribute("message");
  session.removeAttribute("errorMessage");

  double totaleCarrello = 0.0;
  for (Map.Entry<CartItemBean, ProdottoBean> entry : cartItemsWithProducts.entrySet()) {
    ProdottoBean prodotto = entry.getValue();
  }
%>

<!DOCTYPE html>
<html lang="it">
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/small_logo.png">
  <meta charset="UTF-8">
  <title>Carrello</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/carrello.css?v=1.9">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="cart-page">
  <h2>Il tuo Carrello</h2>


  <div class="cart-table-container">
    <table>
      <thead>
      <tr>
        <th>Prodotto</th>
        <th>Quantità</th>
        <th>Subtotale</th>
        <th></th>
      </tr>
      </thead>
      <tbody>
      <%
        if (cartItemsWithProducts.isEmpty()){
      %>
      <tr>
        <td colspan="4">
          <div class="void-cart">
            <img src="<%= request.getContextPath() %>/img/carrellovuoto.png" alt="Carrello vuoto">
            <div class="void-text">
              <h1>Ops!</h1>
              <h2>Sembra che il carrello sia vuoto</h2>
              <p>Esplora il catalogo e aggiungi prodotti al carrello!</p>
            </div>
          </div>
        </td>
      </tr>
      <% } else {  %>
      <%
        for (Map.Entry<CartItemBean, ProdottoBean> entry : cartItemsWithProducts.entrySet()) {
          CartItemBean item = entry.getKey();
          ProdottoBean prodotto = entry.getValue();
          double subtotaleItem;
          if(!item.isPersonalizzato())
            subtotaleItem= prodotto.getPrezzoFinale() * item.getQuantita();
          else {
            subtotaleItem= (prodotto.getPrezzoFinale()+ ((prodotto.getPrezzoFinale()/100)*20))*item.getQuantita();
          }
          totaleCarrello= totaleCarrello+subtotaleItem;
      %>
      <tr class="product-row">
        <td>
          <div class="cart-info">
            <a href="DettaglioProdottoServlet?id=<%= prodotto.getId() %>" class="product-image">
              <img src="<%= prodotto.getImgPath() %>" alt="Prodotto Carrello">
            </a>
            <div class="product-details">
              <p><%= prodotto.getNome() %></p>
              <small>Taglia: <%= prodotto.getTaglia() %></small><br>
              <small>Colore: <%= prodotto.getColore() %></small><br>
              <% if(!item.isPersonalizzato()) {%>
              <small>Prezzo Unità: €<%= String.format("%.2f", prodotto.getPrezzoFinale()) %></small>
              <%}else{%>
              <small>Personalizzato <b>+20%</b></small><br>
              <small>Prezzo Unità: €<%= String.format("%.2f", prodotto.getPrezzoFinale()+((prodotto.getPrezzoFinale()/100)*20)) %></small>
              <%}%>
            </div>
          </div>
        </td>
        <td>
          <input type="number" value="<%= item.getQuantita() %>" min="1" data-product-id="<%= item.getProductID() %>" data-product-size="<%= prodotto.getTaglia() %>">
        </td>
        <td class="subtotal-cell">€ <%= String.format("%.2f", subtotaleItem) %></td>
        <td>
          <form action="CarrelloServlet" method="post" class="remove-form">
            <input type="hidden" name="action" value="rimuovi">
            <input type="hidden" name="idProdotto" value="<%= item.getProductID() %>">
            <input type="hidden" name="taglia" value="<%= URLEncoder.encode(prodotto.getTaglia(), StandardCharsets.UTF_8.toString()) %>">
            <button type="submit" class="remove-item-btn">Rimuovi</button>
          </form>
        </td>
      </tr>
      <% } %>
      <% } %>
      </tbody>
    </table>
  </div>

  <div class="total-container">
    <% if (!cartItemsWithProducts.isEmpty()) { %>
    <table>
      <tr>
        <td>Totale Carrello:</td>
        <td>€ <%= String.format("%.2f", totaleCarrello) %></td>
      </tr>
    </table>
    <button class="checkout-btn">Procedi all'acquisto</button>
    <% } else { %>
    <a href="CatalogoServlet" class="checkout-btn">Vai al Catalogo</a>
    <% } %>
  </div>
</div>

<jsp:include page="footer.jsp" />
<script src="./scripts/carrello.js"></script>
</body>
</html>