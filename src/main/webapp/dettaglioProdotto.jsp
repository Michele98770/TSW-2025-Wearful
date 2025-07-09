<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>

<%
  ProdottoBean mainProduct = (ProdottoBean) request.getAttribute("mainProduct");
  Set<String> availableColors = (Set<String>) request.getAttribute("availableColors");
  Map<String, List<String>> sizesByColor = (Map<String, List<String>>) request.getAttribute("sizesByColor");
  Map<String, Map<String, ProdottoBean>> productsByColorAndSize = (Map<String, Map<String, ProdottoBean>>) request.getAttribute("productsByColorAndSize");
  String selectedColor = (String) request.getAttribute("selectedColor");
  String selectedSize = (String) request.getAttribute("selectedSize");
  String productGroupName = (String) request.getAttribute("productGroupName");
  String jsonProducts = (String) request.getAttribute("jsonProducts");

  if (mainProduct == null) {
    response.sendRedirect(request.getContextPath() + "/CatalogoServlet");
    return;
  }
%>

<!DOCTYPE html>
<html lang="it">
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta charset="UTF-8">
  <title><%= productGroupName %> - Dettaglio Prodotto</title>
  <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/small_logo.png">
  <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/dettaglioProdotto.css?v=1.5">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="main-content product-detail-layout">
  <div class="product-image-gallery">
    <div class="product-image-container">
      <img id="productImage" src="<%= mainProduct.getImgPath() %>" alt="<%= mainProduct.getNome() %>">
      <img id="customImagePreview" class="custom-image-preview" src="">
    </div>
  </div>

  <div class="product-details-container">
    <h1 id="productName"><%= productGroupName %></h1>
    <% if (!mainProduct.isPersonalizzabile()){ %>
    <p class="product-description" id="productDescription"><%= mainProduct.getDescrizione() %></p>
    <% }%>
    <p class="product-price" id="productPrice">€ <%= String.format("%.2f", mainProduct.getPrezzoFinale()) %></p>

    <div class="variant-selector color-selector">
      <h3>Colore: <span id="selectedColorDisplay"><%= selectedColor %></span></h3>
      <div class="color-options">
        <% for (String color : availableColors) { %>
        <div class="color-option <% if(color.equals(selectedColor)) out.print("selected"); %>"
             data-color="<%= color %>"
             style="background-color: <%= productsByColorAndSize.get(color).values().iterator().next().getCodiceColore() %>;"
             title="<%= color %>">
        </div>
        <% } %>
      </div>
    </div>

    <div class="variant-selector size-selector">
      <h3>Taglia: <span id="selectedSizeDisplay"></span></h3>
      <div class="size-options">
        <%
          List<String> currentSizesForSelectedColor = sizesByColor.get(selectedColor);
          if (currentSizesForSelectedColor != null) {
            for (String size : currentSizesForSelectedColor) {
        %>
        <div class="size-option <% if(size.equals(selectedSize)) out.print("selected"); %> <% if(productsByColorAndSize.get(selectedColor).get(size).getDisponibilita() <= 0) out.print("unavailable"); %>"
             data-size="<%= size %>">
          <%= size %>
        </div>
        <%
            }
          }
        %>
      </div>
      <p id="availabilityStatus" class="availability-status <% if(mainProduct.getDisponibilita() <= 0) out.print("out-of-stock"); %>">
        <% if(mainProduct.getDisponibilita() > 0) { %>
        Disponibili: <%= mainProduct.getDisponibilita() %>
        <% } else { %>
        Esaurito
        <% } %>
      </p>
    </div>

    <form action="<%= request.getContextPath() %>/CarrelloServlet" method="post" class="add-to-cart-form">
      <input type="hidden" name="action" value="aggiungi">
      <input type="hidden" name="idProdotto" id="selectedProductId" value="<%= mainProduct.getId() %>">
      <div class="quantity-selector">
        <label for="quantity">Quantità:</label>
        <input type="number" id="quantity" name="quantita" value="1" min="1" max="<%= mainProduct.getDisponibilita() %>" <%= mainProduct.getDisponibilita() <= 0 ? "disabled" : "" %>>
      </div>

      <% if(mainProduct.isPersonalizzabile()) { %>
      <div class="custom-file-upload">
        <label for="fileUploadBtn" class="upload-button">
          Aggiungi Immagine
        </label>
        <input type="file" id="fileUploadBtn" accept="image/*">
      </div>
      <% }%>
      <button type="submit" class="btn btn-primary add-to-cart-btn" <%= mainProduct.getDisponibilita() <= 0 ? "disabled" : "" %>>Aggiungi al Carrello</button>
    </form>
  </div>
</div>

<jsp:include page="footer.jsp" />

<script>
  const productsData = <%= jsonProducts %>;
</script>
<script src="<%= request.getContextPath() %>/scripts/prodotto.js?v=1.5"></script>
<script src="<%= request.getContextPath() %>/scripts/personalizza.js?v=1.0"></script>

</body>
</html>