<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.ordine.OrdineBean" %>
<%@ page import="model.utente.UtenteBean" %>
<%@ page import="model.orderitem.OrderItemBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.concurrent.TimeUnit" %>

<%
    List<OrdineBean> tuttiGliOrdini = (List<OrdineBean>) request.getAttribute("tuttiGliOrdini");
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
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/ordiniAdmin.css?v=1.3">
</head>
<body class="admin-orders-page">

<jsp:include page="header.jsp" />

<div class="admin-orders-container">
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
    <div class="table-wrapper">
        <table class="orders-table">
            <thead>
            <tr>
                <th>ID Ordine</th>
                <th>Cliente</th>
                <th>Data Ordine</th>
                <th>Totale</th>
                <th>Stato</th>
                <th>Azione</th>
            </tr>
            </thead>
            <tbody>
            <% for (OrdineBean ordine : tuttiGliOrdini) {
                UtenteBean cliente = utentiPerOrdine.get(ordine.getId());

                double totaleOrdineConIva = 0.0;
                List<Map<String, Object>> currentOrderItems = itemsPerOrdine.get(ordine.getId());
                if (currentOrderItems != null) {
                    for (Map<String, Object> itemMap : currentOrderItems) {
                        OrderItemBean orderItem = (OrderItemBean) itemMap.get("orderItem");
                        if (orderItem != null) {
                            totaleOrdineConIva += (orderItem.getPrezzo() * orderItem.getQuantita()) * (1 + orderItem.getIva() / 100.0);
                        }
                    }
                }
            %>
            <tr>
                <td data-label="ID Ordine" class="clickable-cell" onclick="window.location='<%= request.getContextPath() %>/DettaglioOrdineServlet?idOrdine=<%= ordine.getId() %>'">
                    #<%= ordine.getId() %>
                </td>
                <td data-label="Cliente" class="clickable-cell" onclick="window.location='<%= request.getContextPath() %>/DettaglioOrdineServlet?idOrdine=<%= ordine.getId() %>'">
                    <% if (cliente != null) { %><%= cliente.getEmail() %><% } else { %>N/D<% } %>
                </td>
                <td data-label="Data Ordine" class="clickable-cell" onclick="window.location='<%= request.getContextPath() %>/DettaglioOrdineServlet?idOrdine=<%= ordine.getId() %>'">
                    <%= dateFormatter.format(ordine.getDataOrdine()) %>
                </td>
                <td data-label="Totale" class="clickable-cell" onclick="window.location='<%= request.getContextPath() %>/DettaglioOrdineServlet?idOrdine=<%= ordine.getId() %>'">
                    <%= currencyFormatter.format(totaleOrdineConIva) %>
                </td>
                <td data-label="Stato">
                    <select class="status-select" onchange="updateOrderStatus(this, <%= ordine.getId() %>)">
                        <option value="Spedito" <%= "Spedito".equals(ordine.getStato()) ? "selected" : "" %>>Spedito</option>
                        <option value="In Consegna" <%= "In Consegna".equals(ordine.getStato()) ? "selected" : "" %>>In Consegna</option>
                        <option value="Consegnato" <%= "Consegnato".equals(ordine.getStato()) ? "selected" : "" %>>Consegnato</option>
                    </select>
                </td>
                <td data-label="Azione">
                    <a href="RimuoviOrdineServlet?idOrdine=<%= ordine.getId() %>"
                       class="btn-remove"
                       onclick="return confirm('Sei sicuro di voler eliminare definitivamente questo ordine? L\'azione è irreversibile.');">
                        Rimuovi
                    </a>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
    <% } %>
</div>

<jsp:include page="footer.jsp" />

<script>
    function updateOrderStatus(selectElement, orderId) {
        const newStatus = selectElement.value;
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '<%= request.getContextPath() %>/UpdateOrderServlet';

        const orderIdInput = document.createElement('input');
        orderIdInput.type = 'hidden';
        orderIdInput.name = 'idOrdine';
        orderIdInput.value = orderId;
        form.appendChild(orderIdInput);

        const statusInput = document.createElement('input');
        statusInput.type = 'hidden';
        statusInput.name = 'nuovoStato';
        statusInput.value = newStatus;
        form.appendChild(statusInput);

        document.body.appendChild(form);
        form.submit();
    }
</script>

</body>
</html>