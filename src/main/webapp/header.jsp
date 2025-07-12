<%@ page import="model.utente.UtenteBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<header class="header">
    <link rel="stylesheet" type="text/css" href="./stylesheets/stileheader.css?v=1.6">
    <link rel="stylesheet" type="text/css" href="./stylesheets/common.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script>
        const CONTEXT_PATH = "<%= request.getContextPath() %>";
    </script>
    <script src="./scripts/search.js" defer></script>
    <script src="./scripts/menu.js" defer></script>
    <script src="./scripts/mobile.js"></script>

    <div class="logo">
        <a href="<%= request.getContextPath() %>/">
            <img src="img/wide_logo.png" alt="Logo Desktop" class="logo-desktop"/>
            <img src="img/small_logo.png" alt="Logo Mobile" class="logo-mobile"/>
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
            <input id="search" type="text" placeholder="Cerca..." value="<%= request.getAttribute("searchQuery") != null ? (String)request.getAttribute("searchQuery") : "" %>">
            <i class="material-icons">search</i>
        </div>

        <%
            UtenteBean user = (UtenteBean) session.getAttribute("user");
            if (user == null) {
        %>
        <a href="login.jsp" class="accedi-desktop">Accedi</a>
        <% } else { %>
        <div class="dropdown">
            <a href="#" class="dropdown-toggle">
                <i class="material-icons">person</i>
            </a>
            <div class="dropdown-menu">
                <a href="<%= request.getContextPath() %>/AreaRiservataServlet">Il mio account</a>
                <a href="<%= request.getContextPath() %>/OrdiniServlet">I miei ordini</a>
                <form action="<%= request.getContextPath() %>/LogoutServlet" method="post" class="logout-form">
                    <button type="submit" class="logout-button">
                        <i class="material-icons">logout</i> Logout
                    </button>
                </form>
            </div>
        </div>
        <% } %>

        <a href="CarrelloServlet" id="cart" class="cart-icon">
            <i class="material-icons">shopping_cart</i>

            <%Integer cartCount = (Integer) session.getAttribute("cartCount");
                if (cartCount != null && cartCount > 0) { %>
            <span class="cart-count">
                <%out.print(cartCount);%>
            </span>
            <%}%>
        </a>

        <button class="menu-toggle" aria-label="Apri menu">
            <i class="material-icons">menu</i>
        </button>
    </div>
</header>
<div class="overlay"></div>
<nav class="mobile-menu" id="mobile-menu-panel">
    <a id="catalogo-mobile" href="CatalogoServlet">Catalogo</a>
    <a href="./chiSiamo.jsp">Chi siamo</a>
    <a href="./domande.jsp">Domande</a>
    <a href="./contatti.jsp">Contatti</a>

    <div class="mobile-menu-separator"></div>

    <%
        if (user == null) {
    %>
    <a href="login.jsp" class="mobile-menu-login">
        <i class="material-icons">login</i> Accedi
    </a>
    <% } else { %>
    <a href="<%= request.getContextPath() %>/AreaRiservataServlet">
        <i class="material-icons">person_outline</i> Il mio account
    </a>
    <a href="<%= request.getContextPath() %>/OrdiniServlet">
        <i class="material-icons">receipt_long</i> I miei ordini
    </a>
    <form action="<%= request.getContextPath() %>/LogoutServlet" method="post" class="logout-form">
        <button type="submit" class="logout-button">
            <i class="material-icons">logout</i> Logout
        </button>
    </form>
    <% } %>
</nav>