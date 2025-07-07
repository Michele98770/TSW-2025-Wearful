<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.gruppoprodotti.GruppoProdottiBean" %>
<%@ page import="model.gruppoprodotti.GruppoProdottiDAO" %>
<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="model.prodotto.ProdottoDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.ArrayList" %>

<%
  GruppoProdottiDAO gruppoDAO = new GruppoProdottiDAO();
  List<GruppoProdottiBean> gruppi = new ArrayList<>();
  String errorMessage = null;
  String successMessage = null;

  try {
    gruppi = gruppoDAO.doRetrieveAll();
  } catch (SQLException e) {
    errorMessage = "Errore database nel recupero dei gruppi di prodotti: " + e.getMessage();
    System.err.println("SQL Exception in adminGroups.jsp (doRetrieveAll): " + e.getMessage());
    e.printStackTrace();
  }

  String selectedGroupId = request.getParameter("groupId");
  GruppoProdottiBean selectedGroup = null;
  List<ProdottoBean> prodottiGruppo = null;

  long groupID = -1;

  if (selectedGroupId != null && !selectedGroupId.isEmpty()) {
    try {
      groupID = Long.parseLong(selectedGroupId);
      selectedGroup = gruppoDAO.doRetrieveByKey(groupID);
      if (selectedGroup == null) {
        errorMessage = "Il gruppo prodotti con ID " + selectedGroupId + " non è stato trovato.";
      } else {
        ProdottoDAO prodottoDAO = new ProdottoDAO();
        try {
          prodottiGruppo = prodottoDAO.doRetrieveByGruppo(selectedGroup.getId());
        } catch (SQLException e) {
          errorMessage = "Errore database nel recupero dei prodotti del gruppo: " + e.getMessage();
          System.err.println("SQL Exception in adminGroups.jsp (doRetrieveByGruppo): " + e.getMessage());
          e.printStackTrace();
        }
      }
    } catch (NumberFormatException e) {
      errorMessage = "ID gruppo prodotto non valido. Si prega di inserire un numero valido.";
      System.err.println("NumberFormatException in adminGroups.jsp: " + selectedGroupId + ". Error: " + e.getMessage());
      e.printStackTrace();
    } catch (SQLException e) {
      errorMessage = "Errore database nel recupero del gruppo prodotti: " + e.getMessage();
      System.err.println("SQL Exception in adminGroups.jsp (doRetrieveByKey): " + e.getMessage());
      e.printStackTrace();
    }
  }
%>

<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <title>Modifica Linea di Prodotti</title>
  <link rel="icon" type="image/png" href="./img/small_logo.png">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="./stylesheets/admin.css?v=1.2">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="admin-form-container">
  <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
  <div class="admin-feedback-messages admin-error-message">
    <p><%= errorMessage %></p>
  </div>
  <% } %>
  <% if (successMessage != null && !successMessage.isEmpty()) { %>
  <div class="admin-feedback-messages admin-success-message">
    <p><%= successMessage %></p>
  </div>
  <% } %>

  <h2>Gestione Linea di Prodotti</h2>

  <table class="admin-table">
    <thead>
    <tr>
      <th>Nome Linea Prodotti</th>
      <th>Azioni</th>
    </tr>
    </thead>
    <tbody>
    <% if (gruppi != null && !gruppi.isEmpty()) { %>
    <% for (GruppoProdottiBean gruppo : gruppi) { %>
    <tr>
      <td><%= gruppo.getNome() %></td>
      <td>
        <form method="get" action="">
          <input type="hidden" name="groupId" value="<%= gruppo.getId() %>">
          <button type="submit" class="admin-action-button">Visualizza Prodotti</button>
        </form>
      </td>
    </tr>
    <% } %>
    <% } else { %>
    <tr>
      <td colspan="2">Nessuna linea di prodotti trovato.</td>
    </tr>
    <% } %>
    </tbody>
  </table>
  <% if (selectedGroup != null) { %>
  <h3 class="admin-group-title">Prodotti della Linea: <%= selectedGroup.getNome() %></h3>

  <% if (prodottiGruppo != null && !prodottiGruppo.isEmpty()) { %>
  <table class="admin-table">
    <thead>
    <tr>
      <th>Nome</th>
      <th>Taglia</th>
      <th>Quantità</th>
      <th>Azioni</th>
    </tr>
    </thead>
    <tbody>
    <% for (ProdottoBean prodotto : prodottiGruppo) { %>
    <tr>
      <td><%= prodotto.getNome() %></td>
        <td><%= prodotto.getTaglia() %></td>
      <td>
        <form method="post" action="AdminUpdateProductQuantityServlet" class="admin-quantity-form">
          <input type="hidden" name="productId" value="<%= prodotto.getId() %>">
          <input type="number" name="newQuantity" value="<%= prodotto.getDisponibilita() %>" min="0" required>
          <button type="submit" class="admin-action-button">Aggiorna</button>
        </form>
      </td>
      <td>
        <form method="post" action="DeleteProductServlet" onsubmit="return confirm('Sicuro di voler rimuovere questo prodotto?');">
          <input type="hidden" name="productId" value="<%= prodotto.getId() %>">
          <button type="submit" class="admin-action-button danger">Rimuovi</button>
        </form>
      </td>
    </tr>
    <% } %>
    </tbody>
  </table>
  <% } else { %>
  <p>Questa linea non ha ancora prodotti.</p>
  <% } %>
  <form method="get" action="AdminUploadServlet">
    <input type="hidden" name="id_gruppo" value="<%= selectedGroup.getId() %>">
    <button type="submit" id="edit" class="admin-submit-button">Aggiungi Prodotto a questa Linea</button>
  </form>
  <% } %>
</div>
<jsp:include page="footer.jsp" />
</body>
</html>