<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.ordine.OrdineBean" %>
<%@ page import="model.utente.UtenteBean" %>
<%@ page import="model.orderitem.OrderItemBean" %>
<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.concurrent.TimeUnit" %>
<%@ page import="model.ordine.OrdineDAO" %>

<%
    OrdineDAO ordineDAO= new OrdineDAO();
    List<OrdineBean> tuttiGliOrdini = ordineDAO.doRetrieveAll();
    Map<Long, List<Map<String, Object>>> itemsPerOrdine = (Map<Long, List<Map<String, Object>>>) request.getAttribute("itemsPerOrdine");
    Map<Long, UtenteBean> utentiPerOrdine = (Map<Long, UtenteBean>) request.getAttribute("utentiPerOrdine");

    if (tuttiGliOrdini == null) {
        tuttiGliOrdini = new java.util.ArrayList<>();
    }
    if (itemsPerOrdine == null) {
        itemsPerOrdine = new LinkedHashMap<>();
    }
    if (utentiPerOrdine == null) {
        utentiPerOrdine = new LinkedHashMap<>();
    }

    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.ITALY);
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    Date currentDate = new Date();
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/small_logo.png">
    <meta charset="UTF-8">
    <title>Gestione Ordini</title>
    <%-- Link al nuovo file CSS per l'admin --%>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/ordiniAdmin.css?v=1.0">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="admin-orders-page">
    <h2>Gestione Ordini</h2>

    <% if (tuttiGliOrdini.isEmpty()) { %>
    <div class="no-orders">
        <img src="<%= request.getContextPath() %>/img/no_ordini.png" alt="Nessun ordine nel sistema">
        <div class="no-orders-text">
            <h1>Tutto tace...</h1>
            <h2>Non ci sono ancora ordini nel sistema.</h2>
            <p>Appena un cliente effettuerà un acquisto, lo vedrai comparire qui.</p>
        </div>
        <a href="AdminDashboardServlet" class="dashboard-btn">Torna alla Dashboard</a>
    </div>
    <% } else { %>
    <div class="orders-list">
        <% for (OrdineBean ordine : tuttiGliOrdini) {
            UtenteBean cliente = utentiPerOrdine.get(ordine.getId());

            double totaleOrdineConIva = 0.0;
            List<Map<String, Object>> currentOrderItemsWithProducts = itemsPerOrdine.get(ordine.getId());

            if (currentOrderItemsWithProducts != null) {
                for (Map<String, Object> itemMap : currentOrderItemsWithProducts) {
                    OrderItemBean orderItem = (OrderItemBean) itemMap.get("orderItem");
                    if (orderItem != null) {
                        totaleOrdineConIva += orderItem.getTotaleConIva();
                    }
                }
            }

            long diffInMillies = Math.abs(currentDate.getTime() - ordine.getDataOrdine().getTime());
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            String orderStatus = "";
            String statusClass = "";
            if (diffInDays >= 3) {
                orderStatus = "Consegnato";
                statusClass = "delivered";
            } else if(diffInDays==2) {
                orderStatus="In Consegna";
                statusClass="pending";
            }else{
                orderStatus = "Spedito";
                statusClass = "shipped";
            }
        %>
        <a href="<%= request.getContextPath() %>/DettaglioOrdineServlet?idOrdine=<%= ordine.getId() %>" class="order-card-link">
            <div class="order-card">
                <div class="order-header">
                    <h3>Ordine #<%= ordine.getId() %></h3>
                    <span class="order-date">Data: <%= dateFormatter.format(ordine.getDataOrdine()) %></span>
                </div>
                <div class="order-details-grid">
                    <div class="detail-item user-info-detail">
                        <strong>Cliente:</strong> <% if (cliente != null) { %> <%= cliente.getEmail() %> <% } else { %> Non disponibile <% } %>
                    </div>
                    <div class="detail-item">
                        <strong>Stato:</strong>
                        <span class="status <%= statusClass %>"><%= orderStatus %></span>
                    </div>
                    <div class="detail-item">
                        <strong>Totale:</strong> <%= currencyFormatter.format(totaleOrdineConIva) %>
                    </div>
                    <div class="detail-item">
                        <strong>Info Consegna ID:</strong> <%= ordine.getInfoConsegna() %>
                    </div>
                </div>

                <div class="order-items">
                    <h4>Articoli dell'Ordine:</h4>
                    <% if (currentOrderItemsWithProducts != null && !currentOrderItemsWithProducts.isEmpty()) { %>
                    <ul>
                        <% for (Map<String, Object> itemMap : currentOrderItemsWithProducts) {
                            OrderItemBean orderItem = (OrderItemBean) itemMap.get("orderItem");
                            ProdottoBean product = (ProdottoBean) itemMap.get("product");
                            if (orderItem != null && product != null) {
                        %>
                        <li>
                            <img src="<%= product.getImgPath() %>" alt="<%= product.getNome() %>">
                            <div class="item-info">
                                <p><%= product.getNome() %></p>
                                <small>Quantità: <%= orderItem.getQuantita() %> | Prezzo Unitario (IVA esclusa): <%= currencyFormatter.format(orderItem.getPrezzo()) %></small><br>
                                <small>IVA: <%= orderItem.getIva() %>% | Subtotale (IVA inclusa): <%= currencyFormatter.format(orderItem.getTotaleConIva()) %></small>
                            </div>
                        </li>
                        <% } %>
                        <% } %>
                    </ul>
                    <% } else { %>
                    <p>Nessun prodotto trovato per questo ordine.</p>
                    <% } %>
                </div>
            </div>
        </a>
        <% } %>
    </div>
    <% } %>
</div>

<jsp:include page="footer.jsp" />
</body>
</html>