<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import=" java.util.List" %>
<%@ page import="model.prodotto.ProdottoDAO" %>
<%@ page import="model.prodotto.ProdottoBean" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product Management</title>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .form-group { margin-bottom: 15px; }
    </style>
</head>
<body>
    <h1 align="center">Product Management</h1>
    
    <%-- Add Product Form --%>
    <form action="product" method="post">
        <div class="form-group">
            <label>Name:</label>
            <input type="text" name="name" required>
        </div>
        <div class="form-group">
            <label>Price:</label>
            <input type="number" step="0.01" name="price" required>
        </div>
        <div class="form-group">
            <label>Quantity:</label>
            <input type="number" name="quantity" required>
        </div>
        <input type="hidden" name="action" value="add">
        <button type="submit">Add Product</button>
    </form>

    <%-- Product List --%>
    <h2>Product List</h2>
    <table>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Price</th>
            <th>Quantity</th>
        </tr>
        <%  try{
            ProdottoDAO productDAO = new ProdottoDAO();
            List<ProdottoBean> products = productDAO.doRetrieveAll();
            for (ProdottoBean product : products) {
        %>
        <tr>
            <td><%= product.getId() %></td>
            <td><%= product.getNome() %></td>
            <td><%= product.getPrezzo() %></td>
            <td><%= product.getDisponibilita() %></td>
            <td>
                <form action="product" method="post" style="display:inline;">
                    <input type="hidden" name="id" value="<%= product.getId() %>">
                    <input type="hidden" name="action" value="delete">
                    <button type="submit">Delete</button>
                </form>
            </td>
        </tr>
        <% }
            } catch (Exception e) {
                String error= String.valueOf(e);
                System.out.println(error);
        }%>

    </table>
</body>
</html>
