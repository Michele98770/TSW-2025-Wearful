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
    <title>Catalogo Prodotti - Wearful</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f8f9fa;
        }
        .container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 20px;
        }
        h1 {
            color: #343a40;
            text-align: center;
            margin-bottom: 30px;
            font-weight: 600;
        }
        .product-table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            margin-top: 20px;
            background-color: white;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            border-radius: 8px;
            overflow: hidden;
        }
        .product-table thead {
            background-color: #343a40;
            color: white;
        }
        .product-table th, .product-table td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #e0e0e0;
        }
        .product-table th {
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.85rem;
            letter-spacing: 0.5px;
        }
        .product-table tbody tr:hover {
            background-color: #f8f9fa;
            transition: background-color 0.2s ease;
        }
        .product-table tbody tr:last-child td {
            border-bottom: none;
        }
        .product-img {
            width: 80px;
            height: 80px;
            object-fit: cover;
            border-radius: 4px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .action-buttons {
            display: flex;
            gap: 8px;
        }
        .btn {
            padding: 8px 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 500;
            font-size: 0.85rem;
            transition: all 0.2s ease;
        }
        .btn-details {
            background-color: #17a2b8;
            color: white;
        }
        .btn-details:hover {
            background-color: #138496;
        }
        .btn-add {
            background-color: #28a745;
            color: white;
        }
        .btn-add:hover {
            background-color: #218838;
        }
        .availability {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 0.8rem;
            font-weight: 500;
        }
        .in-stock {
            background-color: #d4edda;
            color: #155724;
        }
        .out-of-stock {
            background-color: #f8d7da;
            color: #721c24;
        }
        .price {
            font-weight: 600;
            color: #28a745;
        }
        .no-products {
            text-align: center;
            padding: 40px;
            color: #6c757d;
            font-size: 1.1rem;
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
                <img src="<%= prodotto.getImgPath() %>" alt="<%= prodotto.getNome() %>" class="product-img">
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

<script>
    function addToCart(productId, productName) {
        // Implementa la logica AJAX per aggiungere al carrello
        fetch('AddToCartServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `productId=${productId}&quantity=1`
        })
            .then(response => response.json())
            .then(data => {
                if(data.success) {
                    showNotification(`"${productName}" aggiunto al carrello!`);
                } else {
                    showNotification("Errore: " + data.message, true);
                }
            })
            .catch(error => {
                showNotification("Errore durante l'operazione", true);
            });
    }

    function showNotification(message, isError = false) {
        const notification = document.createElement('div');
        notification.style.position = 'fixed';
        notification.style.bottom = '20px';
        notification.style.right = '20px';
        notification.style.padding = '15px 25px';
        notification.style.backgroundColor = isError ? '#dc3545' : '#28a745';
        notification.style.color = 'white';
        notification.style.borderRadius = '4px';
        notification.style.boxShadow = '0 4px 6px rgba(0,0,0,0.1)';
        notification.style.zIndex = '1000';
        notification.style.transition = 'all 0.3s ease';
        notification.textContent = message;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.style.opacity = '0';
            setTimeout(() => {
                document.body.removeChild(notification);
            }, 300);
        }, 3000);
    }
</script>
</body>
</html>