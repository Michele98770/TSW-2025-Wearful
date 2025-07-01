<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>

<%
  ProdottoBean mainProduct = (ProdottoBean) request.getAttribute("mainProduct");
  List<ProdottoBean> allVariants = (List<ProdottoBean>) request.getAttribute("allVariants");
  Set<String> availableColors = (Set<String>) request.getAttribute("availableColors");
  Map<String, List<String>> sizesByColor = (Map<String, List<String>>) request.getAttribute("sizesByColor");
  Map<String, Map<String, ProdottoBean>> productsByColorAndSize = (Map<String, Map<String, ProdottoBean>>) request.getAttribute("productsByColorAndSize");
  String selectedColor = (String) request.getAttribute("selectedColor");
  String selectedSize = (String) request.getAttribute("selectedSize");

  if (mainProduct == null) {
    response.sendRedirect(request.getContextPath() + "/CatalogoServlet");
    return;
  }

  StringBuilder jsonProducts = new StringBuilder("{");
  boolean firstColor = true;
  for (Map.Entry<String, Map<String, ProdottoBean>> colorEntry : productsByColorAndSize.entrySet()) {
    if (!firstColor) jsonProducts.append(",");
    jsonProducts.append("\"").append(colorEntry.getKey()).append("\":{");
    boolean firstSize = true;
    for (Map.Entry<String, ProdottoBean> sizeEntry : colorEntry.getValue().entrySet()) {
      if (!firstSize) jsonProducts.append(",");
      ProdottoBean pb = sizeEntry.getValue();
      jsonProducts.append("\"").append(sizeEntry.getKey()).append("\":{");
      jsonProducts.append("\"id\":").append(pb.getId()).append(",");
      jsonProducts.append("\"nome\":\"").append(pb.getNome().replace("\"", "\\\"")).append("\",");
      jsonProducts.append("\"descrizione\":\"").append(pb.getDescrizione().replace("\"", "\\\"")).append("\",");
      jsonProducts.append("\"prezzo\":").append(pb.getPrezzo()).append(",");
      jsonProducts.append("\"disponibilita\":").append(pb.getDisponibilita()).append(",");
      jsonProducts.append("\"imgPath\":\"").append(pb.getImgPath().replace("\"", "\\\"")).append("\"");
      jsonProducts.append("}");
      firstSize = false;
    }
    jsonProducts.append("}");
    firstColor = false;
  }
  jsonProducts.append("}");
%>

<!DOCTYPE html>
<html lang="it">
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta charset="UTF-8">
  <title><%= mainProduct.getNome() %> - Dettaglio Prodotto</title>
  <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/small_logo.png">
  <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/dettaglioProdotto.css?v=1.0">

</head>
<body>

<jsp:include page="header.jsp" />

<div class="main-content product-detail-layout">
  <div class="product-image-gallery">
    <img id="productImage" src="<%= mainProduct.getImgPath() %>" alt="<%= mainProduct.getNome() %>">
  </div>

  <div class="product-details-container">
    <h1 id="productName"><%= mainProduct.getNome() %></h1>
    <p class="product-description" id="productDescription"><%= mainProduct.getDescrizione() %></p>
    <p class="product-price" id="productPrice">€ <%= String.format("%.2f", mainProduct.getPrezzo()) %></p>

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
        <% List<String> currentSizesForSelectedColor = sizesByColor.get(selectedColor);
          if (currentSizesForSelectedColor != null) {
            for (String size : currentSizesForSelectedColor) { %>
        <div class="size-option <% if(size.equals(selectedSize)) out.print("selected"); %> <% if(productsByColorAndSize.get(selectedColor).get(size).getDisponibilita() <= 0) out.print("unavailable"); %>"
             data-size="<%= size %>">
          <%= size %>
        </div>
        <% }
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

    <form action="<%= request.getContextPath() %>/AggiungiAlCarrelloServlet" method="post" class="add-to-cart-form">
      <input type="hidden" name="productId" id="selectedProductId" value="<%= mainProduct.getId() %>">
      <div class="quantity-selector">
        <label for="quantity">Quantità:</label>
        <input type="number" id="quantity" name="quantity" value="1" min="1" max="<%= mainProduct.getDisponibilita() %>" <%= mainProduct.getDisponibilita() <= 0 ? "disabled" : "" %>>
      </div>
      <button type="submit" class="btn btn-primary add-to-cart-btn" <%= mainProduct.getDisponibilita() <= 0 ? "disabled" : "" %>>Aggiungi al Carrello</button>
    </form>
  </div>
</div>

<jsp:include page="footer.jsp" />

<script>
  const productsData = <%= jsonProducts.toString() %>;
</script>
<script src="<%= request.getContextPath() %>/scripts/prodotto.js?v=1.0"></script>

</body>
</html>