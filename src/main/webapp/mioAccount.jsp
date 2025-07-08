<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.utente.UtenteBean" %>
<%@ page import="model.infoconsegna.InfoConsegnaBean" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Area Personale</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/png" href="./img/small_logo.png">
  <link rel="stylesheet" type="text/css" href="./stylesheets/mioAccount.css?v=2.1">
</head>
<body>
<jsp:include page="header.jsp"/>
<main class="container-user-area">

  <%
    UtenteBean user = (UtenteBean) request.getAttribute("user");
    List<InfoConsegnaBean> infoConsegne = (List<InfoConsegnaBean>) request.getAttribute("infoConsegne");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
  %>

  <h1 class="welcome-heading">Benvenuto, <%= user.getUsername() %>!</h1>

  <% if (successMessage != null) { %>
  <div class="message success-message"><%= successMessage %></div>
  <% } %>
  <% if (errorMessage != null) { %>
  <div class="message error-message"><%= errorMessage %></div>
  <% } %>

  <% if (user.isAdmin()) { %>
  <div class="admin-buttons">
    <a href="./adminUpload.jsp">
      <button type="button" class="add-button">Aggiungi Nuovi Prodotti</button>
    </a>
    <a href="./adminEdit.jsp">
      <button type="button" class="add-button">Modifica Prodotti</button>
    </a>
  </div>

  <% } %>

  <h2 class="section-heading">Informazioni Personali</h2>
  <div class="info-display">
    <p><strong>Email:</strong> <%= user.getEmail() %></p>
    <p><strong>Nome Utente:</strong> <%= user.getUsername() %></p>
    <p><strong>Telefono:</strong> <%= user.getTelefono() %></p>
  </div>

  <h2 class="section-heading">I Tuoi Indirizzi di Consegna</h2>
  <% if (infoConsegne != null && !infoConsegne.isEmpty()) { %>
  <% for (InfoConsegnaBean infoConsegna : infoConsegne) { %>
  <div class="address-card">
    <div class="address-info">
      <p><strong>Destinatario:</strong> <%= infoConsegna.getDestinatario() %></p>
      <p><strong>Via:</strong> <%= infoConsegna.getVia() %></p>
      <p><strong>Città:</strong> <%= infoConsegna.getCitta() %></p>
      <p><strong>CAP:</strong> <%= infoConsegna.getCap() %></p>
      <p><strong>Altro:</strong> <%= infoConsegna.getAltro() != null ? infoConsegna.getAltro() : "" %></p>
    </div>
    <div class="address-actions">
      <button class="edit-button" onclick="mostraForm(
              '<%= infoConsegna.getId() %>',
              '<%= infoConsegna.getDestinatario().replace("'", "\\'") %>',
              '<%= infoConsegna.getVia().replace("'", "\\'") %>',
              '<%= infoConsegna.getCitta().replace("'", "\\'") %>',
              '<%= infoConsegna.getCap() %>',
              '<%= (infoConsegna.getAltro() != null ? infoConsegna.getAltro().replace("'", "\\'") : "") %>'
              )">Modifica</button>
      <form action="AreaRiservataServlet" method="post" class="remove-form" onsubmit="return confirm('Sei sicuro di voler rimuovere questo indirizzo?');">
        <input type="hidden" name="idConsegnaRimuovi" value="<%= infoConsegna.getId() %>">
        <button type="submit" class="remove-button">Rimuovi</button>
      </form>
    </div>
  </div>
  <% } %>
  <button id="addAddressBtn" class="add-button">Aggiungi Nuovo Indirizzo</button>
  <% } else { %>
  <p class="no-addresses-message">Nessun indirizzo di consegna registrato.</p>
  <button id="addAddressBtn" class="add-button" onclick="mostraForm()">Aggiungi Indirizzo</button>
  <% } %>

  <form action="AreaRiservataServlet" method="post" class="form-section" id="form-section" style="display:none;">
    <input type="hidden" name="idConsegna" id="idConsegna">
    <div class="form-group-user">
      <label for="destinatario">Nome Destinatario:</label>
      <input type="text" id="destinatario" name="destinatario" required>
    </div>
    <div class="form-group-user">
      <label for="via">Via/Piazza (e numero civico):</label>
      <input type="text" id="via" name="via" required>
    </div>
    <div class="form-group-user">
      <label for="citta">Città:</label>
      <input type="text" id="citta" name="citta" required>
    </div>
    <div class="form-group-user">
      <label for="cap">CAP:</label>
      <input type="number" id="cap" name="cap" required>
    </div>
    <div class="form-group-user">
      <label for="altro">Altre informazioni (es. Scala, Interno):</label>
      <input type="text" id="altro" name="altro">
    </div>
    <div id="form-error" class="form-error-message"></div>
    <div class="form-group-user">
      <input type="submit" value="Aggiungi Indirizzo" class="submit-button" id="submit-button" disabled>
    </div>
  </form>

  <p class="back-link"><a href="CatalogoServlet">Torna alla Home</a></p>
</main>
<jsp:include page="footer.jsp"/>
<script src="./scripts/mioAccount.js"></script>
</body>
</html>
