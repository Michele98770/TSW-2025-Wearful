<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="java.util.List" %>

<%
    List<ProdottoBean> representativeProducts = (List<ProdottoBean>) request.getAttribute("representativeProducts");
    String jsonProductsData = (String) request.getAttribute("jsonProductsData");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="UTF-8">
    <title>Scegli e Personalizza</title>
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/small_logo.png">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/dettaglioProdotto.css?v=1.6">
    <style>
        /* Stili per la griglia di selezione */
        #product-selection-container h2 {
            text-align: center;
            margin-bottom: 30px;
        }

        .grid-container {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            max-width: 1200px;
            margin: 20px auto;
        }

        .product-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            cursor: pointer;
            transition: box-shadow 0.3s ease, transform 0.3s ease;
            flex-basis: calc(25% - 20px); /* 4 colonne */
            box-sizing: border-box;
        }
        .product-card.active {
            border-color: #007bff;
            box-shadow: 0 0 10px rgba(0, 123, 255, 0.5);
            transform: translateY(0);
        }
        .product-card:hover:not(.active) {
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            transform: translateY(-5px);
        }
        .product-card img {
            max-width: 100%;
            height: auto;
            border-bottom: 1px solid #eee;
            margin-bottom: 10px;
        }

        /* Stili per il pannello di personalizzazione */
        .customization-panel-wrapper {
            flex-basis: 100%;
            order: 100; /* Lo spinge in fondo alla riga corrente */
            padding: 20px 0;
        }
        .customization-panel {
            display: flex;
            border: 2px solid #007bff;
            border-radius: 10px;
            padding: 20px;
            background-color: #f9f9f9;
        }
        .close-panel-btn {
            position: absolute;
            top: 15px;
            right: 15px;
            background: none;
            border: none;
            font-size: 24px;
            cursor: pointer;
            font-weight: bold;
            color: #888;
        }
        .close-panel-btn:hover {
            color: #333;
        }

        @media (max-width: 992px) { .product-card { flex-basis: calc(33.33% - 20px); } }
        @media (max-width: 768px) { .product-card { flex-basis: calc(50% - 20px); } }
        @media (max-width: 576px) { .product-card { flex-basis: 100%; } }
    </style>
</head>
<body>

<jsp:include page="header.jsp" />

<div class="main-content">
    <div id="product-selection-container">
        <h2>Scegli il prodotto da personalizzare</h2>
        <div id="product-grid" class="grid-container">
            <% if (representativeProducts != null && !representativeProducts.isEmpty()) {
                for (ProdottoBean product : representativeProducts) { %>
            <div class="product-card" data-group-name="<%= product.getNome() %>">
                <img src="<%= product.getImgPath() %>" alt="<%= product.getNome() %>">
                <h4><%= product.getNome() %></h4>
            </div>
            <% }
            } else { %>
            <p>Nessun prodotto disponibile per la personalizzazione.</p>
            <% } %>

        </div>
    </div>

    <!-- Questo è il TEMPLATE nascosto che verrà clonato e inserito nella griglia -->
    <div id="customization-panel-template" style="display: none;">
        <div class="customization-panel product-detail-layout">
            <div class="custom-image-container">
                <img class="tshirtImage" src="" alt="Maglia personalizzabile">
            </div>

            <div class="product-details-container">
                <h1 class="productName"></h1>
                <p class="product-description"></p>
                <p class="product-price"></p>

                <div class="variant-selector color-selector">
                    <h3>Colore: <span class="selectedColorDisplay"></span></h3>
                    <div class="color-options"></div>
                </div>

                <div class="variant-selector size-selector">
                    <h3>Taglia:</h3>
                    <div class="size-options"></div>
                    <p class="availability-status"></p>
                </div>

                <div class="custom-upload-section">
                    <label>Carica il tuo design (PNG, JPG)</label>
                    <input type="file" class="imageUploader" accept="image/png, image/jpeg, image/webp">
                </div>

                <form action="<%= request.getContextPath() %>/AggiungiAlCarrelloServlet" method="post" enctype="multipart/form-data" class="add-to-cart-form">
                    <input type="hidden" name="productId" class="selectedProductId">
                    <div class="quantity-selector">
                        <label>Quantità:</label>
                        <input type="number" name="quantity" class="quantity" value="1" min="1">
                    </div>
                    <button type="submit" class="btn btn-primary add-to-cart-btn">Aggiungi al Carrello</button>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />

<script>
    const allProductsData = <%= jsonProductsData %>;
</script>
<script src="<%= request.getContextPath() %>/scripts/personalizzaMaglia.js?v=1.3"></script>

</body>
</html>