<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/png" href="./img/small_logo.png">
  <title>Errore 403 - Accesso non autorizzato</title>
  <link rel="stylesheet" href="./stylesheets/error.css">
</head>
<body>
<jsp:include page="header.jsp" />

<br>
<div class="error-container">
  <div class="error-image">
    <img src="<%= request.getContextPath() %>/img/403.png" alt="Non sei autorizzato" style="max-width: 100%; height: auto;">
  </div>
  <div class="error-content">
    <h1>403</h1>
    <h2>Attenzione! Non sei autorizzato ad accedere a questa pagina.</h2>
    <p>Sembra che tu non abbia i permessi per accedere a questa risorsa. Torna indietro.</p>
    <a href="CatalogoServlet">Torna alla Home</a>
  </div>
</div>

<jsp:include page="footer.jsp" />
</body>
</html>