<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <title>Login Utente</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/png" href="./img/small_logo.png">
  <link rel="stylesheet" href="./stylesheets/register.css?v=1.0">

</head>

<body>

<jsp:include page="header.jsp" />

<div class="form-container">
  <h2>Accedi</h2>

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

  <form action="LoginServlet" method="post" class="registration-form" id="loginForm"> <%-- Changed id to loginForm --%>
    <div class="form-group">
      <label for="email" id="emailLabel">Email:</label>
      <input type="email" id="email" name="email" required value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
      <div id="emailError" class="error-message"></div> <%-- Added error div for email --%>
    </div>

    <div class="form-group">
      <label for="password" id="passwordLabel">Password:</label>
      <input type="password" id="password" name="password" required minlength="8">
      <div id="passwordError" class="error-message"></div> <%-- Added error div for password --%>
    </div>

    <button type="submit" class="btn btn-primary">Accedi</button>
  </form>

  <p class="login-link">Non hai un account? <a href="RegisterServlet">Registrati qui</a></p>
</div>

<br><br><br><br><br>

<script src="./scripts/login.js"></script> <%-- Changed script reference to login.js --%>

<jsp:include page="footer.jsp" />

</body>
</html>