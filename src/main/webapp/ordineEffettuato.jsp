<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="./img/small_logo.png">
    <title>Ordine Effettuato!</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/aggiuntaCarrello.css">
</head>
<body>
<jsp:include page="header.jsp" />

<div class="done-container">
    <div class="done-box">
        <div class="done-image">
            <img src="<%= request.getContextPath() %>/img/ordine.png" alt="Ordine Effettuato con Successo">
        </div>

        <div class="done-content">
            <h1>Ordine Effettuato con Successo!</h1>
            <p>Grazie per il tuo acquisto. Il tuo ordine è stato ricevuto e sarà elaborato a breve.</p>

            <div class="done-buttons">
                <a href="<%= request.getContextPath() %>/OrderHistoryServlet" class="primary">Vai ai miei ordini</a>
                <a href="<%= request.getContextPath() %>/CatalogoServlet" class="secondary">Continua lo shopping</a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />
</body>
</html>