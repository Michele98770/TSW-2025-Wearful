<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="java.util.Set" %>

<%
    ProdottoBean mainProduct = (ProdottoBean) request.getAttribute("mainProduct");
    Set<String> availableColors = (Set<String>) request.getAttribute("availableColors");
    String jsonProducts = (String) request.getAttribute("jsonProducts");

    if (mainProduct == null) {
        response.sendRedirect(request.getContextPath() + "/CatalogoServlet");
        return;
    }

    String selectedColor = mainProduct.getColore();
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="UTF-8">
    <title>Personalizza la tua Maglia</title>
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/small_logo.png">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/dettaglioProdotto.css?v=1.4">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/personalizzazione.css?v=1.0">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="main-content product-detail-layout">
    <div class="custom-image-container">
        <img id="tshirtImage" src="<%= mainProduct.getImgPath() %>" alt="Maglia personalizzabile">
        <img id="customOverlayImage" src="" alt="Anteprima personalizzazione">
    </div>

    <div class="product-details-container">
        <h1 id="productName">Maglia Personalizzata</h1>
        <p class="product-description" id="productDescription"><%= mainProduct.getDescrizione() %></p>
        <p class="product-price" id="productPrice">€ <%= String.format("%.2f", mainProduct.getPrezzoFinale()) %></p>

        <div class="variant-selector color-selector">
            <h3>Colore: <span id="selectedColorDisplay"><%= selectedColor %></span></h3>
            <div class="color-options">
                <% for (String color : availableColors) { %>
                <div class="color-option <% if(color.equals(selectedColor)) out.print("selected"); %>"
                     data-color="<%= color %>"
                     style="background-color: <%= color.equalsIgnoreCase("Bianco") ? "#FFFFFF; border: 1px solid #ccc;" : (color.equalsIgnoreCase("Nero") ? "#000000" : color) %>;"
                     title="<%= color %>">
                </div>
                <% } %>
            </div>
        </div>

        <div class="custom-upload-section">
            <label for="imageUploader">Carica il tuo design (PNG, JPG)</label>
            <input type="file" id="imageUploader" name="customImage" accept="image/png, image/jpeg, image/webp">
        </div>

        <form action="<%= request.getContextPath() %>/AggiungiAlCarrelloServlet" method="post" enctype="multipart/form-data" class="add-to-cart-form">
            <input type="hidden" name="productId" id="selectedProductId" value="<%= mainProduct.getId() %>">
            <input type="hidden" id="forwardImageUploader" name="customImageFile">

            <div class="quantity-selector">
                <label for="quantity">Quantità:</label>
                <input type="number" id="quantity" name="quantity" value="1" min="1" max="99">
            </div>
            <button type="submit" class="btn btn-primary add-to-cart-btn">Aggiungi al Carrello</button>
        </form>
    </div>
</div>

<jsp:include page="footer.jsp" />

<script>
    const productsData = <%= jsonProducts %>;
</script>
<script src="<%= request.getContextPath() %>/scripts/personalizzaMaglia.js?v=1.0"></script>

</body>
</html>