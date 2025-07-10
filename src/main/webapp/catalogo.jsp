<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="model.utente.UtenteBean" %>

<%
    UtenteBean utente= (UtenteBean) session.getAttribute("user");
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
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/catalogo.css?v=1.2"> <%-- Ho incrementato la versione per il cache busting --%>
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

<div class="overlay" id="page-overlay"></div>

<div class="catalog-page-layout">
    <aside class="filter-sidebar" id="filter-sidebar">
        <button class="close-sidebar-btn" id="close-sidebar-btn" aria-label="Chiudi filtri">×</button>
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
                <div class="price-inputs"> <%-- Ho aggiunto un div per uno styling potenziale --%>
                    <input type="number" id="minPrice" name="minPrice" placeholder="Min" step="0.01" min="0" value="<%= minPriceStr != null ? minPriceStr : "" %>">
                    <span>-</span>
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

            <button type="submit" class="apply-filters-btn" style="width: 100%; margin-top: 20px; padding: 12px; font-weight: bold; background-color: var(--primary-color); color: white; border: none; border-radius: 5px; cursor: pointer;">Applica Filtri</button>
            <a href="<%= request.getContextPath() %>/CatalogoServlet" class="reset-filters-btn" style="display: block; width: 100%; margin-top: 10px; padding: 12px; text-align: center; background-color: #e0e0e0; color: #333; border: 1px solid #ccc; border-radius: 5px; text-decoration: none;">Reset Filtri</a>
        </form>
    </aside>

    <div class="main-content">
        <%String bannerPath;
        if(utente!=null)
            bannerPath="DettaglioProdottoServlet?id=73";
        else
            bannerPath="./login.jsp";
        %>
        <a href="<%=bannerPath%>"> <img src="./img/banner.jpg" alt="banner" id="banner"></a>

        <div class="mobile-filter-controls">
            <button id="filter-toggle-btn">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M1.5 1.5A.5.5 0 0 1 2 1h12a.5.5 0 0 1 .5.5v2a.5.5 0 0 1-.128.334L10 8.692V13.5a.5.5 0 0 1-.342.474l-3 1.5A.5.5 0 0 1 6 14.5V8.692L1.628 3.834A.5.5 0 0 1 1.5 3.5v-2z"/>
                </svg>
                Filtra
            </button>
        </div>

        <main class="product-grid">
            <% if (products.isEmpty()) { %>
            <div class="no-products-container"> <%-- Contenitore per messaggio --%>
                <img src="./img/search.png" alt="not found" style="max-width: 300px; margin: 0 auto;">
                <p class="no-products-message">Nessun prodotto trovato...</p>
            </div>
            <% } else { %>
            <% for (ProdottoBean product : products) { %>
            <a href="<%= request.getContextPath() %>/DettaglioProdottoServlet?id=<%= product.getId() %>" class="product-card">
                <div class="product-card-image">
                    <img src="<%= product.getImgPath() %>" alt="<%= product.getNome() %>">
                </div>
                <div class="product-card-content">
                    <h4><%= product.getNome() %></h4>
                    <div class="price">€ <%= String.format("%.2f", product.getPrezzoFinale()) %></div>
                </div>
            </a>
            <% } %>
            <% } %>
        </main>

        <%-- La paginazione va dentro main-content per essere associata alla griglia --%>
        <% if (totalPages > 1) { %>
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
        <% } %>
    </div>
</div>

<jsp:include page="footer.jsp" />

<script src="./scripts/catalogo.js"></script>

</body>
</html>