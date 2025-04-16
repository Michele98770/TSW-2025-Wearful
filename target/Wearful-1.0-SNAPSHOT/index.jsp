<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.gruppoprodotti.GruppoProdottiBean" %>
<%@ page import="model.gruppoprodotti.GruppoProdottiDAO" %>
<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="model.prodotto.ProdottoDAO" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Catalogo Prodotti - Wearful</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        h1 {
            color: #333;
            text-align: center;
        }
        .product-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background-color: white;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .product-table th, .product-table td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }
        .product-table th {
            background-color: #f2f2f2;
            position: sticky;
            top: 0;
        }
        .product-table tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        .product-table tr:hover {
            background-color: #f1f1f1;
        }
        .product-img {
            max-width: 100px;
            max-height: 100px;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        .action-buttons {
            display: flex;
            gap: 5px;
        }
        .action-buttons button {
            padding: 5px 10px;
            cursor: pointer;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Catalogo Prodotti</h1>

    <table class="product-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Immagine</th>
            <th>Nome</th>
            <th>Descrizione</th>
            <th>Gruppo</th>
            <th>Taglia</th>
            <th>Colore</th>
            <th>Categoria</th>
            <th>Prezzo</th>
            <th>IVA</th>
            <th>Disponibilità</th>
            <th>Azioni</th>
        </tr>
        </thead>
        <tbody>
        <% try{
            ProdottoDAO prodottoDAO = new ProdottoDAO();
            GruppoProdottiDAO gruppoDAO = new GruppoProdottiDAO();
            List<ProdottoBean> prodotti = prodottoDAO.doRetrieveAll();

            for (ProdottoBean prodotto : prodotti) {
                GruppoProdottiBean gruppo = gruppoDAO.doRetrieveByKey(prodotto.getGruppo());
        %>
        <tr>
            <td><%= prodotto.getId() %></td>
            <td>
                <img src="<%= prodotto.getImgPath() %>" alt="<%= prodotto.getNome() %>" class="product-img">
            </td>
            <td><%= prodotto.getNome() %></td>
            <td><%= prodotto.getDescrizione().length() > 50 ?
                    prodotto.getDescrizione().substring(0, 50) + "..." :
                    prodotto.getDescrizione() %></td>
            <td><%= gruppo != null ? gruppo.getNome() : "N/A" %></td>
            <td><%= prodotto.getTaglia() %></td>
            <td><%= prodotto.getColore() %></td>
            <td><%= prodotto.getCategoria() %></td>
            <td><%= String.format("€%.2f", prodotto.getPrezzo()) %></td>
            <td><%= prodotto.getIva() %>%</td>
            <td><%= prodotto.getDisponibilita() %></td>
            <td class="action-buttons">
                <button onclick="location.href='dettaglioProdotto.jsp?id=<%= prodotto.getId() %>'">Dettagli</button>
                <button onclick="addToCart(<%= prodotto.getId() %>)">Aggiungi</button>
            </td>
        </tr>
        <%
            }
            } catch (Exception ignored) {
            }
        %>
        </tbody>
    </table>
</div>

<script>
    function addToCart(productId) {
        // Implementa la logica per aggiungere al carrello
        alert("Prodotto " + productId + " aggiunto al carrello");
        // Potresti fare una chiamata AJAX qui per aggiungere il prodotto al carrello
    }
</script>
</body>
</html>