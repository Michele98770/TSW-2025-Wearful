<%@ page import="model.utente.UtenteBean" %>
<header class="header">
    <link rel="stylesheet" type="text/css" href="./stylesheets/stileheader.css?v=1.3">
    <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="./stylesheets/common.css?v=1.0">

    <script>
        const CONTEXT_PATH = "<%= request.getContextPath() %>";
    </script>
    <script src="./scripts/search.js"></script> <div class="logo">
    <img src="img/wide_logo.png" alt="Logo Desktop" class="logo-desktop"/>
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
            <i class="material-icons">search</i>
        </div>
        <%
            UtenteBean user= (UtenteBean)session.getAttribute("user");
            if(user==null){ %>
        <a href="login.jsp" id="accedi">Accedi</a>
        <% } else {%>

        <div class="dropdown">
            <a href="#" class="dropdown-toggle">
                <i class="material-icons">person</i>
            </a>
            <div class="dropdown-menu">
                <label><a href="<%= request.getContextPath() %>/AreaRiservataServlet">Il mio account</a></label>
                <label><a href="<%= request.getContextPath() %>/I miei ordiniServlet">I miei ordini</a></label>
                <form action="<%= request.getContextPath() %>/LogoutServlet" method="post" class="logout-form">
                    <button type="submit" class="logout-button">Logout</button>
                </form>
            </div>
        </div>
        <% }%>
        <a href="" id="cart">
            <i class="material-icons" >shopping_cart</i>
        </a>
    </div>
</header>