<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.gruppoprodotti.GruppoProdottiBean" %>
<%@ page import="model.gruppoprodotti.GruppoProdottiDAO" %>
<%@ page import="model.prodotto.ProdottoBean" %>
<%@ page import="model.prodotto.ProdottoDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="stylesheets/admin.css">
</head>
<body>
<div class="container">
    <h1>Catalogo Prodotti</h1>

    <table class="product-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Immagine</th>
            <th>Prodotto</th>
            <th>Descrizione</th>
            <th>Gruppo</th>
            <th>Taglia</th>
            <th>Colore</th>
            <th>Prezzo</th>
            <th>Disponibilità</th>
            <th>Azioni</th>
        </tr>
        </thead>
        <tbody>
        <%
            ProdottoDAO prodottoDAO = new ProdottoDAO();
            GruppoProdottiDAO gruppoDAO = new GruppoProdottiDAO();
            List<ProdottoBean> prodotti = null;
            try {
                prodotti = prodottoDAO.doRetrieveAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (prodotti == null || prodotti.isEmpty()) {
        %>
        <tr>
            <td colspan="10" class="no-products">Nessun prodotto disponibile nel catalogo</td>
        </tr>
        <%
        } else {
            for (ProdottoBean prodotto : prodotti) {
                GruppoProdottiBean gruppo = null;
                try {
                    gruppo = gruppoDAO.doRetrieveByKey(prodotto.getGruppo());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                String disponibilitaClass = prodotto.getDisponibilita() > 0 ? "in-stock" : "out-of-stock";
                String disponibilitaText = prodotto.getDisponibilita() > 0 ?
                        "Disponibile (" + prodotto.getDisponibilita() + ")" : "Esaurito";
        %>
        <tr>
            <td><%= prodotto.getId() %></td>
            <td>
                <img src="img/wide_logo.png" alt="<%= prodotto.getNome() %>" class="product-img">
            </td>
            <td>
                <strong><%= prodotto.getNome() %></strong><br>
                <small class="text-muted"><%= prodotto.getCategoria() %></small>
            </td>
            <td>
                <%= prodotto.getDescrizione().length() > 60 ?
                        prodotto.getDescrizione().substring(0, 60) + "..." :
                        prodotto.getDescrizione() %>
            </td>
            <td><%= gruppo != null ? gruppo.getNome() : "N/A" %></td>
            <td><%= prodotto.getTaglia() %></td>
            <td><%= prodotto.getColore() %></td>
            <td class="price"><%= String.format("€%.2f", prodotto.getPrezzo()) %></td>
            <td>
        <span class="availability <%= disponibilitaClass %>">
            <%= disponibilitaText %>
        </span>
            </td>
            <td class="action-buttons">
                <button class="btn btn-details"
                        onclick="window.location.href='dettaglioProdotto.jsp?id=<%= prodotto.getId() %>'">
                    Dettagli
                </button>
                <% if (prodotto.getDisponibilita() > 0) { %>
                <button class="btn btn-add"
                        onclick="addToCart(<%= prodotto.getId() %>, '<%= prodotto.getNome() %>')">
                    Aggiungi
                </button>
                <% } else { %>
                <button class="btn btn-add" disabled>
                    Non disponibile
                </button>
                <% } %>
            </td>
        </tr>
        <%
                } // Fine del ciclo for
            } // Fine dell'else
        %>
        </tbody>
    </table>
</div>
</body>
</html>