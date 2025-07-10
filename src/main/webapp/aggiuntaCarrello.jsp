<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="model.cartitem.CartItemBean" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<%
    ProdottoBean prodotto = (ProdottoBean) request.getAttribute("prodotto");
    CartItemBean item= (CartItemBean) request.getAttribute("item");
%>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="./img/small_logo.png">
    <title>Prodotto Aggiunto!</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/aggiuntaCarrello.css">
</head>
<body>
<jsp:include page="header.jsp" />

<div class="done-container">
    <div class="done-box">

        <% if (prodotto != null && item != null) { %>

        <div class="done-image">
            <img src="<%= request.getContextPath() %>/img/aggiuntaCarrello.png" alt="Prodotto Aggiunto con Successo">
        </div>

        <div class="done-content">
            <h1>Aggiunto al carrello!</h1>

            <div class="added-product-card">
                <div class="added-product-image">
                    <img src="<%= request.getContextPath() %>/<%= prodotto.getImgPath() %>" alt="<%= prodotto.getNome() %>">
                </div>
                <div class="added-product-details">
                    <%if(!item.isPersonalizzato()){%>
                    <h4><%= prodotto.getNome() %></h4>
                    <span class="price"><%= String.format("€ %.2f", prodotto.getPrezzoFinale()) %></span>
                    <%} else{%>
                    <h4><%= prodotto.getNome()%> <b> Personalizzato</b></h4>
                    <span class="price"><%= String.format("€ %.2f", prodotto.getPrezzoFinale()+((prodotto.getPrezzoFinale()/100)*20)) %></span>
                    <%}%>
                </div>
            </div>

            <p>Il prodotto è stato aggiunto correttamente. Cosa vuoi fare adesso?</p>

            <div class="done-buttons">
                <a href="CarrelloServlet" class="primary">Vai al carrello</a>
                <a href="CatalogoServlet" class="secondary">Continua lo shopping</a>
            </div>
        </div>

        <% } else { %>
        <div class="done-content" style="text-align:center; width:100%;">
            <h1>Operazione completata</h1>
            <p>C'è stato un problema nel recuperare i dettagli del prodotto.</p>
            <div class="done-buttons">
                <a href="CarrelloServlet" class="primary">Vai al carrello</a>
                <a href="CatalogoServlet" class="secondary">Torna al Catalogo</a>
            </div>
        </div>
        <% } %>

    </div>
</div>

<jsp:include page="footer.jsp" />
</body>
</html>