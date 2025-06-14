<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Registrazione Utente</title>
    <link rel="icon" type="image/png" href="./img/small_logo.png">
    <link rel="stylesheet" href="./stylesheets/stilefooter.css">
    <link rel="stylesheet" href="./stylesheets/stileheader.css">
    <link rel="stylesheet" href="./stylesheets/common.css">
    <link rel="stylesheet" href="./stylesheets/register.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="https://kit.fontawesome.com/4c2f47ebbf.js" crossorigin="anonymous"></script>
</head>
<body>

<jsp:include page="header.jsp" />

<div class="form-container">
    <h2>Registrati</h2>

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

    <form action="RegisterServlet" method="post" class="registration-form">
        <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
        </div>

        <div class="form-group">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required maxlength="50" value="<%= request.getParameter("username") != null ? request.getParameter("username") : "" %>">
        </div>

        <div class="form-group">
            <label for="telefono">Telefono:</label>
            <input type="tel" id="telefono" name="telefono" required maxlength="13" value="<%= request.getParameter("telefono") != null ? request.getParameter("telefono") : "" %>">
        </div>

        <div class="form-group">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required minlength="8">
        </div>

        <div class="form-group">
            <label for="confirm_password">Conferma Password:</label>
            <input type="password" id="confirm_password" name="confirm_password" required minlength="8">
        </div>

        <button type="submit" class="btn btn-primary">Registrati</button>
    </form>

    <p class="login-link">Hai già un account? <a href="login.jsp">Accedi qui</a></p>
</div>

<jsp:include page="footer.jsp" />

<script src="./scripts/menu.js"></script>
</body>
</html>