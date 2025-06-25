<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Errore 404 - Pagina Non Trovata</title>

    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/small_logo.png">
    <link rel="stylesheet" href="./stylesheets/stilefooter.css">
    <link rel="stylesheet" href="./stylesheets/stileheader.css">
    <link rel="stylesheet" href="./stylesheets/common.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="https://kit.fontawesome.com/4c2f47ebbf.js" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="./stylesheets/error.css">
</head>
<body>
<jsp:include page="header.jsp" />

<br>
<div class="error-container">
    <div class="error-image">
        <img src="<%= request.getContextPath() %>/img/500.png" alt="Pagina non trovata" style="max-width: 100%; height: auto;">
    </div>
    <div class="error-content">
        <h1>500</h1>
        <h2>Qualcosa è adato storto...</h2>
        <p>Siamo spiacenti. riprova più tardi.
        </p>
        <a href="<%= request.getContextPath() %>/login.jsp">Torna alla Home</a>
    </div>
</div>

<jsp:include page="footer.jsp" />
</body>
</html>