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

    // Variabili per mantenere i valori del form in caso di errore
    String oldProductName = (String) request.getAttribute("oldProductName");
    String oldProductDescription = (String) request.getAttribute("oldProductDescription");
    String oldProductTaglia = (String) request.getAttribute("oldProductTaglia");
    String oldProductColore = (String) request.getAttribute("oldProductColore");
    String oldProductCategory = (String) request.getAttribute("oldProductCategory");
    String oldProductPrice = (String) request.getAttribute("oldProductPrice");
    String oldProductIva = (String) request.getAttribute("oldProductIva");
    String oldProductDisponibilita = (String) request.getAttribute("oldProductDisponibilita");
    boolean oldProductPersonalizzabile = (Boolean) (request.getAttribute("oldProductPersonalizzabile") != null ? request.getAttribute("oldProductPersonalizzabile") : false);
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Amministrazione Prodotti</title>
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/small_logo.png">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/stilefooter.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/stileheader.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/common.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="https://kit.fontawesome.com/4c2f47ebbf.js" crossorigin="anonymous"></script>

    <style>
        /* Stili aggiuntivi per posizionare l'anteprima */
        .admin-page-layout {
            display: flex;
            justify-content: space-between; /* Spazia gli elementi */
            align-items: flex-start; /* Allinea in alto */
            gap: 40px; /* Spazio tra il form e l'anteprima */
            padding: 20px;
            max-width: 1200px; /* Limita la larghezza totale */
            margin: 20px auto; /* Centra il layout */
        }

        .form-section {
            flex: 1; /* Il form prenderà lo spazio disponibile */
            min-width: 400px; /* Larghezza minima per il form */
        }

        #imagePreviewContainer {
            flex-shrink: 0; /* Impedisce al contenitore di restringersi */
            width: 350px; /* Larghezza fissa per l'anteprima */
            border: 1px solid #ccc;
            padding: 15px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            background-color: #fff;
            display: none; /* Inizialmente nascosto, JavaScript lo mostrerà */
            text-align: center;
            border-radius: 8px;
            margin-top: 50px; /* Allinea con il titolo del form */
        }

        #colorDropperCanvas {
            display: block; /* Rimuove lo spazio extra sotto il canvas */
            max-width: 100%; /* Si adatta alla larghezza del contenitore */
            height: auto;
            border: 1px solid #eee;
            cursor: crosshair;
            margin: 10px auto; /* Centra il canvas */
            border-radius: 4px;
        }

        #selectedColorDisplay {
            margin-top: 15px;
            padding: 10px;
            border: 1px solid #eee;
            background-color: #f9f9f9;
            border-radius: 4px;
        }

        #currentColorSwatch {
            width: 30px;
            height: 30px;
            border: 1px solid #ccc;
            display: inline-block;
            vertical-align: middle;
            margin-left: 10px;
            border-radius: 4px;
        }
    </style>
</head>
<body>

<jsp:include page="header.jsp" />

<div class="main-admin-content">
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="feedback-messages error">
        <p><%= errorMessage %></p>
    </div>
    <% } %>
    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="feedback-messages success">
        <p><%= successMessage %></p>
    </div>
    <% } %>
    <% if (!validationErrors.isEmpty()) { %>
    <div class="feedback-messages error">
        <ul>
            <% for (String error : validationErrors) { %>
            <li><%= error %></li>
            <% } %>
        </ul>
    </div>
    <% } %>

    <div class="admin-page-layout">
        <div class="form-section">
            <h2>Aggiungi Prodotto</h2>
            <form action="<%= request.getContextPath() %>/AdminUploadServlet" method="post" class="registration-form" enctype="multipart/form-data">
                <input type="hidden" name="formType" value="newProduct">

                <div class="form-group">
                    <label for="groupName">Nome Gruppo Prodotto:</label>
                    <input type="text" id="groupName" name="groupName" required maxlength="255"
                           value="<%= groupExists ? currentSelectedGroup.getNome() : (request.getAttribute("oldGroupName") != null ? request.getAttribute("oldGroupName") : "") %>"
                        <%= groupExists ? "readonly" : "" %>>
                    <% if (groupExists) { %>
                    <input type="hidden" name="groupId" value="<%= currentSelectedGroup.getId() %>">
                    <% } %>
                </div>

                <div class="form-group">
                    <label for="productName">Nome Prodotto:</label>
                    <input type="text" id="productName" name="productName" required maxlength="255"
                           value="<%= oldProductName != null ? oldProductName : "" %>">
                </div>

                <div class="form-group">
                    <label for="productDescription">Descrizione:</label>
                    <textarea id="productDescription" name="productDescription" rows="5" required maxlength="4096"><%= oldProductDescription != null ? oldProductDescription : "" %></textarea>
                </div>

                <div class="form-group">
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

                <div class="form-group">
                    <label for="productColore">Colore:</label>
                    <div class="color-input-group">
                        <input type="text" id="productColore" name="productColore" required maxlength="50"
                               value="<%= oldProductColore != null ? oldProductColore : "" %>">
                        <input type="color" id="colorPicker" value="<%= oldProductColore != null && !oldProductColore.isEmpty() ? oldProductColore : "#000000" %>">
                    </div>
                </div>

                <div class="form-group">
                    <label for="productCategoria">Categoria:</label>
                    <input type="text" id="productCategoria" name="productCategoria" required maxlength="50"
                           value="<%= oldProductCategory != null ? oldProductCategory : "" %>">
                </div>

                <div class="form-group">
                    <label for="productPrezzo">Prezzo:</label>
                    <input type="number" id="productPrezzo" name="productPrezzo" step="0.01" min="0" required
                           value="<%= oldProductPrice != null ? oldProductPrice : "" %>">
                </div>

                <div class="form-group">
                    <label for="productIVA">IVA (%):</label>
                    <select id="productIVA" name="productIVA" required>
                        <option value="">-- Seleziona IVA --</option>
                        <option value="4" <%= "4".equals(oldProductIva) ? "selected" : "" %>>4%</option>
                        <option value="10" <%= "10".equals(oldProductIva) ? "selected" : "" %>>10%</option>
                        <option value="22" <%= "22".equals(oldProductIva) ? "selected" : "" %>>22%</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="productDisponibilita">Disponibilità:</label>
                    <input type="number" id="productDisponibilita" name="productDisponibilita" min="0" required
                           value="<%= oldProductDisponibilita != null ? oldProductDisponibilita : "" %>">
                </div>

                <div class="radio-group">
                    <label>Personalizzabile:</label>
                    <input type="radio" id="personalizzabileYes" name="personalizzabile" value="true" <%= oldProductPersonalizzabile ? "checked" : "" %>>
                    <label for="personalizzabileYes">Sì</label>
                    <input type="radio" id="personalizzabileNo" name="personalizzabile" value="false" <%= !oldProductPersonalizzabile ? "checked" : "" %>>
                    <label for="personalizzabileNo">No</label>
                </div>

                <div class="form-group">
                    <label for="productImgFile">Carica Immagine Prodotto:</label>
                    <input type="file" id="productImgFile" name="productImgFile" accept="image/*" required>
                </div>

                <button type="submit" class="btn btn-primary">Aggiungi Prodotto e/o Crea Gruppo</button>
            </form>
        </div>

        <div id="imagePreviewContainer">
            <h3>Anteprima Immagine e Contagocce</h3>
            <p>Clicca sull'immagine per scegliere un colore.</p>
            <canvas id="colorDropperCanvas"></canvas>
            <div id="selectedColorDisplay">
                Colore Selezionato: <span id="currentColorHex">#------</span>
                <div id="currentColorSwatch"></div>
            </div>
        </div>
    </div>
</div>

<script src="<%= request.getContextPath() %>/scripts/upload.js"></script>
<jsp:include page="footer.jsp" />

</body>
</html>