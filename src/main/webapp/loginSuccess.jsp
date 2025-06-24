<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Login Effettuato</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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

<div class="main-content-wrapper">
    <div class="success-container">
        <h2>Successo!</h2>
        <p><%= request.getAttribute("loginStatus") %></p>
        <a href="<%= request.getContextPath() %>/" class="back-home-link">Torna alla Home</a>
    </div>
</div>

<jsp:include page="footer.jsp" />

</body>
</html>