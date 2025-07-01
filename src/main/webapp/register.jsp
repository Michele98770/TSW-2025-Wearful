<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Registrazione Utente</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="./img/small_logo.png">
    <link rel="stylesheet" href="./stylesheets/register.css?v=1.0">
<style>
    .input-error {
        border-color: #dc3545 !important;
    }
    .input-success {
        border-color: #28a745 !important;
    }
    .label-error {
        color: #dc3545 ;
    }
    .label-success {
        color: #28a745 ;
    }

    .error-messages {
        background-color: #f8d7da;
        color: #721c24 !important;
        padding: 10px;
        border-radius: 5px;
        margin-bottom: 15px;
        text-align: center;
    }
    .success-messages {
        background-color: #d4edda;
        color: #155724;
        padding: 10px;
        border-radius: 5px;
        margin-bottom: 15px;
        text-align: center;
    }

    .error-text {
        color: #dc3545;
        font-size: 0.85em;
        margin-top: 5px;
        text-align: left;
        display: none; /* Inizialmente nascosto */
    }
</style>
</head>
<body>

<jsp:include page="header.jsp" />

<div class="form-container">
    <h2>Registrati</h2>


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

    <form action="RegisterServlet" method="post" class="registration-form" id="registrationForm">
        <div class="form-group">
            <label for="email" id="emailLabel">Email:</label>
            <input type="email" id="email" name="email" placeholder="es. email@example.com" required value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
            <div id="emailError" class="error-text"></div>
        </div>

        <div class="form-group">
            <label for="username" id="usernameLabel">Username:</label>
            <input type="text" id="username" name="username" required maxlength="50" value="<%= request.getParameter("username") != null ? request.getParameter("username") : "" %>">
            <div id="usernameError" class="error-text"></div>
        </div>

        <div class="form-group">
            <label for="telefono" id="telefonoLabel">Telefono:</label>
            <input type="tel" id="telefono" name="telefono" placeholder="es. +397398379481" maxlength="13" value="<%= request.getParameter("telefono") != null ? request.getParameter("telefono") : "" %>">
            <div id="telefonoError" class="error-text"></div>
        </div>

        <div class="form-group">
            <label for="password" id="passwordLabel">Password:</label>
            <input type="password" id="password" name="password" required minlength="8">
            <div id="passwordError" class="error-text"></div>
        </div>

        <div class="form-group">
            <label for="confirm_password" id="confirmPasswordLabel">Conferma Password:</label>
            <input type="password" id="confirm_password" name="confirm_password" required minlength="8">
            <div id="confirmPasswordError" class="error-text"></div>
        </div>

        <button type="submit" class="btn btn-primary">Registrati</button>
    </form>

    <p class="login-link">Hai già un account? <a href="login.jsp">Accedi qui</a></p>
</div>

<jsp:include page="footer.jsp" />

<script src="./scripts/register.js"></script>
</body>
</html>