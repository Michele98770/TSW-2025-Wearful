<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <title>Login Utente</title>
  <link rel="icon" type="image/png" href="./img/small_logo.png">
  <link rel="stylesheet" href="./stylesheets/stilefooter.css">
  <link rel="stylesheet" href="./stylesheets/stileheader.css">
  <link rel="stylesheet" href="./stylesheets/common.css">
  <link rel="stylesheet" href="./stylesheets/register.css"> <%-- Usa lo stesso foglio di stile per il form --%>
  <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
  <script src="https://kit.fontawesome.com/4c2f47ebbf.js" crossorigin="anonymous"></script>
</head>
<body>

<jsp:include page="header.jsp" />

<div class="form-container">
  <h2>Accedi</h2> <%-- Titolo cambiato per il login --%>

  <%-- Messaggi di errore o successo --%>
  <%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
    if (errorMessage != null) {
  %>
  <div class="error-messages">
    <p><%= errorMessage %></p>
  </div>
  <%
  } else if (successMessage != null) {
  %>
  <div class="success-messages">
    <p><%= successMessage %></p>
  </div>
  <%
    }
  %>

  <form action="LoginServlet" method="post" class="registration-form"> <%-- Action per il LoginServlet, stessa classe form --%>
    <div class="form-group">
      <label for="email">Email:</label>
      <input type="email" id="email" name="email" required value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
    </div>

    <div class="form-group">
      <label for="password">Password:</label>
      <input type="password" id="password" name="password" required>
    </div>

    <button type="submit" class="btn btn-primary">Accedi</button> <%-- Testo del bottone cambiato --%>
  </form>

  <p class="login-link">Non hai un account? <a href="RegisterServlet">Registrati qui</a></p> <%-- Link per la registrazione --%>
</div>

<br><br><br><br><br>

<jsp:include page="footer.jsp" />

<script src="./scripts/menu.js"></script>
</body>
</html>
