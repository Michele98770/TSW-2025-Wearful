<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>

<%
    List<ProdottoBean> products = (List<ProdottoBean>) request.getAttribute("products");
    if (products == null) {
        products = new ArrayList<>();
    }

    String currentCategory = (String) request.getAttribute("filterCategory");

    String minPriceStr = (String) request.getAttribute("filterMinPrice");
    String maxPriceStr = (String) request.getAttribute("filterMaxPrice");

    List<String> selectedSizes = (List<String>) request.getAttribute("filterSizes");
    if (selectedSizes == null) selectedSizes = new ArrayList<>();

    String currentSearchQuery = (String) request.getAttribute("searchQuery");
    if (currentSearchQuery == null) currentSearchQuery = "";
    List<String> availableSizes = new ArrayList<>();
    availableSizes.add("XXS");
    availableSizes.add("XS");
    availableSizes.add("S");
    availableSizes.add("M");
    availableSizes.add("L");
    availableSizes.add("XL");
    availableSizes.add("XXL");

    int currentPage = (Integer) (request.getAttribute("currentPage") != null ? request.getAttribute("currentPage") : 1);
    int totalPages = (Integer) (request.getAttribute("totalPages") != null ? request.getAttribute("totalPages") : 1);
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="./img/small_logo.png">
    <meta charset="UTF-8">
    <title>Catalogo Prodotti</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/catalogo.css?v=1.0">
</head>
<body>

<jsp:include page="header.jsp" />

<%
    String username = (String) session.getAttribute("welcomeMessageUsername");
    if (username != null) {
%>
<div class="welcome-message">
    Benvenuto, <%= username %>!
</div>
<%
        session.removeAttribute("welcomeMessageUsername");
    }
%>


<div class="main-content catalog-page-layout">
    <aside class="filter-sidebar">
        <h3>Filtra Prodotti</h3>
        <form action="<%= request.getContextPath() %>/CatalogoServlet" method="get">
            <input type="hidden" name="page" value="1">
            <input type="hidden" name="action" value="filter">

            <input type="hidden" name="searchQuery" value="<%= currentSearchQuery %>">

            <div class="filter-group category-filters">
                <label>Categorie:</label>
                <button type="submit" name="category" value="" class="<%= (currentCategory == null || currentCategory.isEmpty()) ? "active" : "" %>">Tutte le Categorie</button>
                <button type="submit" name="category" value="Maglia" class="<%= "Maglia".equals(currentCategory) ? "active" : "" %>">Maglie</button>
                <button type="submit" name="category" value="Felpa" class="<%= "Felpa".equals(currentCategory) ? "active" : "" %>">Felpe</button>
                <button type="submit" name="category" value="Cappello" class="<%= "Cappello".equals(currentCategory) ? "active" : "" %>">Cappelli</button>
            </div>

            <div class="filter-group price-filter">
                <label for="minPrice">Prezzo:</label>
                <div>
                    <input type="number" id="minPrice" name="minPrice" placeholder="Min" step="0.01" min="0" value="<%= minPriceStr != null ? minPriceStr : "" %>">
                    <input type="number" id="maxPrice" name="maxPrice" placeholder="Max" step="0.01" min="0" value="<%= maxPriceStr != null ? maxPriceStr : "" %>">
                </div>
            </div>

            <div class="filter-group size-filter">
                <label>Taglia:</label>
                <div class="size-filter-options">
                    <% for (String size : availableSizes) {
                        boolean isSelected = selectedSizes.contains(size);
                    %>
                    <div class="size-option">
                        <input type="checkbox" id="size-<%= size %>" name="size" value="<%= size %>" <%= isSelected ? "checked" : "" %>>
                        <label for="size-<%= size %>"><%= size %></label>
                    </div>
                    <% } %>
                </div>
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 20px;">Applica Filtri</button>
            <button type="reset" class="btn btn-secondary" style="width: 100%; margin-top: 10px;" onclick="window.location.href='<%= request.getContextPath() %>/CatalogoServlet'">Reset Filtri</button>
        </form>
    </aside>

    <main class="product-grid">
        <% if (products.isEmpty()) { %>
        <img src="./img/search.png" alt="not found" style="width: 100%;">
        <p class="no-products-message">Nessun prodotto trovato...</p>
        <% } else { %>
        <% for (ProdottoBean product : products) {
            if(!product.isPersonalizzabile()){
        %>
        <a href="<%= request.getContextPath() %>/DettaglioProdottoServlet?id=<%= product.getId() %>" class="product-card">
            <div class="product-card-image">
                <%
                    String finalImagePath = product.getImgPath();
                %>
                <img src="<%= finalImagePath %>" alt="<%= product.getNome() %>">
            </div>
            <div class="product-card-content">
                <h4><%= product.getNome() %></h4>
                <div class="price">€ <%= String.format("%.2f", product.getPrezzoFinale()) %></div>
            </div>
        </a>
        <% } %>
        <% } }%>
    </main>
</div>

<div class="pagination">
    <% for (int i = 1; i <= totalPages; i++) {
        StringBuilder queryStringBuilder = new StringBuilder("?page=" + i);
        queryStringBuilder.append("&action=filter");

        if (currentCategory != null && !currentCategory.isEmpty()) queryStringBuilder.append("&category=").append(URLEncoder.encode(currentCategory, StandardCharsets.UTF_8.toString()));
        if (minPriceStr != null && !minPriceStr.isEmpty()) queryStringBuilder.append("&minPrice=").append(URLEncoder.encode(minPriceStr, StandardCharsets.UTF_8.toString()));
        if (maxPriceStr != null && !maxPriceStr.isEmpty()) queryStringBuilder.append("&maxPrice=").append(URLEncoder.encode(maxPriceStr, StandardCharsets.UTF_8.toString()));
        for (String size : selectedSizes) {
            queryStringBuilder.append("&size=").append(URLEncoder.encode(size, StandardCharsets.UTF_8.toString()));
        }

        if (currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
            queryStringBuilder.append("&searchQuery=").append(URLEncoder.encode(currentSearchQuery, StandardCharsets.UTF_8.toString()));
        }
        String queryString = queryStringBuilder.toString();
    %>
    <% if (i == currentPage) { %>
    <span class="current-page"><%= i %></span>
    <% } else { %>
    <a href="<%= request.getContextPath() %>/CatalogoServlet<%= queryString %>"><%= i %></a>
    <% } %>
    <% } %>
</div>


<jsp:include page="footer.jsp" />

</body>
</html>