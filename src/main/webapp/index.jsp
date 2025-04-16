<%@ page import="java.sql.*, model.ConnectionPool, model.EmptyPoolException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String messaggio = "";
    String colore = "black";
    Connection conn = null;

    try {
        // Inizializza la pool una sola volta
        if (application.getAttribute("poolInit") == null) {
            ConnectionPool.init(5);
            application.setAttribute("poolInit", true);
        }

        conn = ConnectionPool.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT 1");

        if (rs.next()) {
            messaggio = "Connessione riuscita ✅";
            colore = "green";
        } else {
            messaggio = "Query eseguita ma nessun risultato 😐";
            colore = "orange";
        }

    } catch (EmptyPoolException | SQLException e) {
        messaggio = "Connessione fallita ❌: " + e.getMessage();
        colore = "red";
    } finally {
        if (conn != null) {
            ConnectionPool.releaseConnection(conn);
        }
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Test Connessione</title>
</head>
<body>
<h1>Test della Connessione al Database</h1>
<p style="color:<%= colore %>;"><%= messaggio %></p>
</body>
</html>
