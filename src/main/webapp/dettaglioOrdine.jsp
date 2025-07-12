<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.ordine.OrdineBean" %>
<%@ page import="model.orderitem.OrderItemBean" %>
<%@ page import="model.infoconsegna.InfoConsegnaBean" %>
<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.concurrent.TimeUnit" %>

<%
  OrdineBean ordine = (OrdineBean) request.getAttribute("ordine");
  List<OrderItemBean> orderItems = (List<OrderItemBean>) request.getAttribute("orderItems");
  InfoConsegnaBean infoConsegna = (InfoConsegnaBean) request.getAttribute("infoConsegna");
  Map<Long, ProdottoBean> prodottiMappa = (Map<Long, ProdottoBean>) request.getAttribute("prodottiMappa");

  NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.ITALY);
  SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
  Date currentDate = new Date(); // Ottieni la data corrente

  double totaleOrdineConIva = 0.0;
  if (orderItems != null) {
    for (OrderItemBean item : orderItems) {
      totaleOrdineConIva += item.getTotaleConIva();
    }
  }

  String orderStatus = "";
  String statusClass = "";
  if (ordine != null && ordine.getDataOrdine() != null) {
    long diffInMillies = Math.abs(currentDate.getTime() - ordine.getDataOrdine().getTime());
    long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

    if (diffInDays >= 3) {
      orderStatus = "Consegnato";
      statusClass = "delivered";
    } else if (diffInDays == 2) {
      orderStatus = "In Consegna";
      statusClass = "pending";
    } else {
      orderStatus = "Spedito";
      statusClass = "shipped";
    }
  }
%>

<!DOCTYPE html>
<html lang="it">
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/small_logo.png">
  <meta charset="UTF-8">
  <title>Dettagli Ordine #<%= ordine != null ? ordine.getId() : "" %></title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/dettaglioOrdine.css?v=1.0">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="order-detail-page">
  <% if (ordine == null) { %>
  <div class="error-message">
    <h3>Ordine non trovato o non accessibile.</h3>
    <p>Torna alla pagina dei <a href="<%= request.getContextPath() %>/OrdiniServlet">Miei Ordini</a>.</p>
  </div>
  <% } else { %>
  <h2>Dettagli dell'Ordine #<%= ordine.getId() %></h2>

  <div class="detail-section order-summary">
    <h3>Riepilogo Ordine</h3>
    <p><strong>Data Ordine:</strong> <%= dateFormatter.format(ordine.getDataOrdine()) %></p>
    <p><strong>Totale Ordine:</strong> <%= currencyFormatter.format(totaleOrdineConIva) %></p>
    <p><strong>Stato:</strong> <span class="status <%= statusClass %>"><%= orderStatus %></span></p>
  </div>

  <div class="detail-section shipping-address">
    <h3>Indirizzo di Consegna</h3>
    <% if (infoConsegna != null) { %>
    <p><%= infoConsegna.getDestinatario() %></p>
    <p><%= infoConsegna.getVia() %> <%= infoConsegna.getAltro() %></p>
    <p><%= infoConsegna.getCap() %> <%= infoConsegna.getCitta() %></p>
    <% } else { %>
    <p>Dettagli indirizzo non disponibili. (ID: <%= ordine.getInfoConsegna() %>)</p>
    <% } %>
  </div>

  <div class="detail-section order-items-list">
    <h3>Articoli dell'Ordine</h3>
    <% if (orderItems != null && !orderItems.isEmpty()) { %>
    <ul class="items-grid">
      <% for (OrderItemBean item : orderItems) {
        ProdottoBean prodottoCorrelato = prodottiMappa.get(item.getIdProdotto());

        String imgPath = (prodottoCorrelato != null && prodottoCorrelato.getImgPath() != null && !prodottoCorrelato.getImgPath().isEmpty())
                ? prodottoCorrelato.getImgPath()
                : request.getContextPath() + "/img/placeholder_product.png";

        String nomeProdotto = (prodottoCorrelato != null) ? prodottoCorrelato.getNome() : item.getNome();
      %>
      <li class="item-card">
        <img src="<%= imgPath %>" alt="<%= nomeProdotto %>">
        <div class="item-info">
          <p class="item-name"><%= nomeProdotto %></p>
          <p>Quantità: <strong><%= item.getQuantita() %></strong></p>
          <p>Prezzo Unitario: <%= currencyFormatter.format(item.getPrezzo()) %> (IVA <%= item.getIva() %>%)</p>
          <p>Subtotale: <strong><%= currencyFormatter.format(item.getTotaleConIva()) %></strong></p>
        </div>
      </li>
      <% } %>
    </ul>
    <% } else { %>
    <p>Nessun articolo trovato per questo ordine.</p>
    <% } %>
  </div>

  <div class="back-link">
    <a href="<%= request.getContextPath() %>/OrdiniServlet" class="button">Torna ai Miei Ordini</a>
  </div>
  <% } %>
</div>

<jsp:include page="footer.jsp" />
</body>
</html>