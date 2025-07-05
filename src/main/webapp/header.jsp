<%@ page import="model.utente.UtenteBean" %>
<header class="header">
    <link rel="stylesheet" type="text/css" href="./stylesheets/stileheader.css?v=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="./stylesheets/common.css">

    <script src="./scripts/search.js"></script>

    <div class="logo">
        <a href="adminUpload.jsp">
            <img src="img/wide_logo.png" alt="Logo Desktop" class="logo-desktop"/>
        </a>
    </div>
    <nav class="menu" id="desktop-menu">
        <a id="catalogo" href="CatalogoServlet">Catalogo</a>
        <a href="./chiSiamo.jsp">Chi siamo</a>
        <a href="./domande.jsp">Domande</a>
        <a href="./contatti.jsp">Contatti</a>
    </nav>
    <div class="user-actions" id="user-actions">
        <div class="box">
            <label>
                <input id="search" type="text" placeholder="Cerca..." value="<%= request.getAttribute("searchQuery") != null ? (String)request.getAttribute("searchQuery") : "" %>">
            </label>
            <a href="#">
                <i class="material-icons">search</i>
            </a>
        </div>
        <%
            UtenteBean user= (UtenteBean)session.getAttribute("user");
            if(user==null){ %>
        <a href="login.jsp">Accedi</a>
        <% } else {%>
        <a href="#">
            <i class="material-icons">person</i>
            <% }%>
        </a>
        <a href="#">
            <i class="material-icons">shopping_cart</i>
        </a>
    </div>
</header>