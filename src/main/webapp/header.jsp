<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<header class="header">
    <div class="logo">
        <a href="#">
            <img src="img/wide_logo.png" alt="Logo"/>
        </a>
    </div>

    <div class="search-bar-mobile">
        <input type="search" placeholder="Cerca...">
        <button type="submit"><i class="material-icons">search</i></button>
    </div>
    <nav class="menu" id="desktop-menu">
        <a href="#">Catalogo</a>
        <a href="#">Chi siamo</a>
        <a href="#">Domande</a>
        <a href="#">Contatti</a>
    </nav>
    <div class="user-actions" id="desktop-user-actions">
        <a href="#">
            <i class="material-icons">search</i>
        </a>
        <select class="language">
            <option value="it">IT</option>
            <option value="en">EN</option>
        </select>
        <a href="#">
            <i class="material-icons">person</i>
        </a>
        <a href="#">
            <i class="material-icons">shopping_cart</i>
        </a>
    </div>

    <div class="hamburger-menu" id="hamburger-icon">
        <div class="bar"></div>
        <div class="bar"></div>
        <div class="bar"></div>
    </div>

    <nav class="mobile-menu-overlay" id="mobile-menu">

        <a href="#">Catalogo</a>
        <a href="#">Chi siamo</a>
        <a href="#">Domande</a>
        <a href="#">Contatti</a>
        <hr>
        <select class="language-mobile">
            <option value="it">IT</option>
            <option value="en">EN</option>
        </select>
        <a href="#"><i class="material-icons">person</i> Accedi</a>
        <a href="#"><i class="material-icons">shopping_cart</i> Carrello</a>
    </nav>
</header>