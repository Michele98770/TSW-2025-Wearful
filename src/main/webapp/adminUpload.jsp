<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.utente.UtenteBean" %>
<%@ page import="model.gruppoprodotti.GruppoProdottiBean" %>
<%@ page import="model.gruppoprodotti.GruppoProdottiDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.SQLException" %>

<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");

    List<String> validationErrors = null;
    Object errorsAttribute = request.getAttribute("validationErrors");
    if (errorsAttribute instanceof List<?>) {
        validationErrors = (List<String>) errorsAttribute;
    }
    if (validationErrors == null) {
        validationErrors = new ArrayList<>();
    }

    GruppoProdottiBean currentSelectedGroup = (GruppoProdottiBean) request.getAttribute("currentSelectedGroup");
    boolean groupExists = (currentSelectedGroup != null && currentSelectedGroup.getId() != null);

    String oldProductName = (String) request.getAttribute("oldProductName");
    String oldProductDescription = (String) request.getAttribute("oldProductDescription");
    String oldProductTaglia = (String) request.getAttribute("oldProductTaglia");
    String oldProductColore = (String) request.getAttribute("oldProductColore");
    String oldProductCodiceColore = (String) request.getAttribute("oldProductCodiceColore");
    String oldProductCategory = (String) request.getAttribute("oldProductCategory");
    String oldProductPrice = (String) request.getAttribute("oldProductPrice");
    String oldProductIva = (String) request.getAttribute("oldProductIva");
    String oldProductDisponibilita = (String) request.getAttribute("oldProductDisponibilita");
    boolean oldProductPersonalizzabile = (Boolean) (request.getAttribute("oldProductPersonalizzabile") != null ? request.getAttribute("oldProductPersonalizzabile") : false);

    String initialColorPickerValue = (oldProductCodiceColore != null && !oldProductCodiceColore.isEmpty()) ? oldProductCodiceColore : "#000000";
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="UTF-8">
    <title>Amministrazione Prodotti</title>
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/small_logo.png">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/stilefooter.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/stileheader.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/common.css?v=1.0">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/admin.css?v=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="https://kit.fontawesome.com/4c2f47ebbf.js" crossorigin="anonymous"></script>

</head>
<body>

<jsp:include page="header.jsp" />

<div id="adminFormWrapper">
    <div class="admin-form-container">

        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="admin-feedback-messages admin-error-message">
            <p><%= errorMessage %></p>
        </div>
        <% } %>
        <% if (successMessage != null && !successMessage.isEmpty()) { %>
        <div class="admin-feedback-messages admin-success-message">
            <p><%= successMessage %></p>
        </div>
        <% } %>
        <% if (!validationErrors.isEmpty()) { %>
        <div class="admin-feedback-messages admin-error-message">
            <ul>
                <% for (String error : validationErrors) { %>
                <li><%= error %></li>
                <% } %>
            </ul>
        </div>
        <% } %>

        <div class="main-admin-content">
            <form action="<%= request.getContextPath() %>/AdminUploadServlet" method="post" class="admin-registration-form" enctype="multipart/form-data">
                <input type="hidden" name="formType" value="newProduct">

                <div class="admin-page-layout">
                    <div class="admin-form-section">
                        <h2>Aggiungi Prodotto</h2>

                        <div class="admin-form-group">
                            <label for="groupName">Nome Linea di prodotti:</label>
                            <input type="text" id="groupName" name="groupName" required maxlength="255"
                                   value="<%= groupExists ? currentSelectedGroup.getNome() : (request.getAttribute("oldGroupName") != null ? request.getAttribute("oldGroupName") : "") %>"
                                <%= groupExists ? "readonly" : "" %>>
                            <% if (groupExists) { %>
                            <input type="hidden" name="groupId" value="<%= currentSelectedGroup.getId() %>">
                            <% } %>
                        </div>

                        <div class="admin-form-group">
                            <label for="productName">Nome Prodotto:</label>
                            <input type="text" id="productName" name="productName" required maxlength="255"
                                   value="<%= oldProductName != null ? oldProductName : "" %>">
                        </div>

                        <div class="admin-form-group">
                            <label for="productDescription">Descrizione:</label>
                            <textarea id="productDescription" name="productDescription" rows="5" required maxlength="4096"><%= oldProductDescription != null ? oldProductDescription : "" %></textarea>
                        </div>

                        <div class="admin-form-group">
                            <label for="productTaglia">Taglia:</label>
                            <select id="productTaglia" name="productTaglia" required>
                                <option value="">-- Seleziona Taglia --</option>
                                <option value="XXS" <%= "XXS".equals(oldProductTaglia) ? "selected" : "" %>>XXS</option>
                                <option value="XS" <%= "XS".equals(oldProductTaglia) ? "selected" : "" %>>XS</option>
                                <option value="S" <%= "S".equals(oldProductTaglia) ? "selected" : "" %>>S</option>
                                <option value="M" <%= "M".equals(oldProductTaglia) ? "selected" : "" %>>M</option>
                                <option value="L" <%= "L".equals(oldProductTaglia) ? "selected" : "" %>>L</option>
                                <option value="XL" <%= "XL".equals(oldProductTaglia) ? "selected" : "" %>>XL</option>
                                <option value="XXL" <%= "XXL".equals(oldProductTaglia) ? "selected" : "" %>>XXL</option>
                            </select>
                        </div>

                        <div class="admin-form-group">
                            <label for="productColore">Nome Colore:</label>
                            <input type="text" id="productColore" name="productColore" required maxlength="50"
                                   value="<%= oldProductColore != null ? oldProductColore : "" %>">
                        </div>

                        <div class="admin-form-group">
                            <label for="productCodiceColore">Colore :</label>
                            <div class="admin-color-input-group">
                                <input type="text" id="productCodiceColore" name="productCodiceColore" required maxlength="7" pattern="#[0-9A-Fa-f]{6}"
                                       value="<%= oldProductCodiceColore != null ? oldProductCodiceColore : "#000000" %>">
                                <input type="color" id="colorPicker" value="<%= initialColorPickerValue %>">
                            </div>
                        </div>

                        <div class="admin-form-group">
                            <label for="productCategoria">Categoria: </label>
                            <select id="productCategoria" name="productCategoria" required>
                                    <option value="">-- Seleziona Categoria --</option>
                                    <option value="Maglia" <%= "Maglia".equals(oldProductTaglia) ? "selected" : "" %>>Maglia</option>
                                    <option value="Felpa" <%= "Felpa".equals(oldProductTaglia) ? "selected" : "" %>>Felpa</option>
                                    <option value="Cappello" <%= "Cappello".equals(oldProductTaglia) ? "selected" : "" %>>Cappello</option>
                                </select>
                        </div>

                        <div class="admin-form-group">
                            <label for="productPrezzo">Prezzo:</label>
                            <input type="number" id="productPrezzo" name="productPrezzo" step="0.01" min="0" required
                                   value="<%= oldProductPrice != null ? oldProductPrice : "" %>">
                        </div>

                        <div class="admin-form-group">
                            <label for="productIVA">IVA (%):</label>
                            <select id="productIVA" name="productIVA" required>
                                <option value="">-- Seleziona IVA --</option>
                                <option value="4" <%= "4".equals(oldProductIva) ? "selected" : "" %>>4%</option>
                                <option value="10" <%= "10".equals(oldProductIva) ? "selected" : "" %>>10%</option>
                                <option value="22" <%= "22".equals(oldProductIva) ? "selected" : "" %>>22%</option>
                            </select>
                        </div>

                        <div class="admin-form-group">
                            <label for="productDisponibilita">Disponibilità:</label>
                            <input type="number" id="productDisponibilita" name="productDisponibilita" min="0" required
                                   value="<%= oldProductDisponibilita != null ? oldProductDisponibilita : "" %>">
                        </div>

                        <div class="admin-radio-group">
                            <label>Personalizzabile:</label>
                            <input type="radio" id="personalizzabileYes" name="personalizzabile" value="true" <%= oldProductPersonalizzabile ? "checked" : "" %>>
                            <label for="personalizzabileYes">Sì</label>
                            <input type="radio" id="personalizzabileNo" name="personalizzabile" value="false" <%= !oldProductPersonalizzabile ? "checked" : "" %>>
                            <label for="personalizzabileNo">No</label>
                        </div>

                    </div>

                    <div id="imagePreviewContainer">
                        <h3>Seleziona il colore con il contagocce</h3>
                        <canvas id="colorDropperCanvas"></canvas>
                        <div id="selectedColorDisplay" style="display: none;">
                            <span id="currentColorHex">#------</span>
                            <div id="currentColorSwatch"></div>
                        </div>
                        <div class="admin-form-group">
                            <label for="productImgFile">Carica Immagine Prodotto:</label>
                            <input type="file" id="productImgFile" name="productImgFile" accept="image/*" required>
                        </div>
                    </div>
                </div>

                <button type="submit" class="admin-submit-button">Aggiungi Prodotto e/o Crea Linea</button>
            </form>
        </div>
    </div>
</div>

<script src="<%= request.getContextPath() %>/scripts/upload.js?v=1.0"></script>
<jsp:include page="footer.jsp" />

</body>
</html>