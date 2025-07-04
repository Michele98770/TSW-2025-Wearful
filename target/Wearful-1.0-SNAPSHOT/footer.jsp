<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Your Page Title</title>
  <link rel="stylesheet" href="./stylesheets/stilefooter.css">
</head>
<body>

<header>
</header>

<div class="main-content-wrapper">
  <br>
</div>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Calendar" %>
<footer class="footer">
  <script src="https://kit.fontawesome.com/4c2f47ebbf.js" crossorigin="anonymous"></script>
  <div class="footer-container">
    <div class="footer-logo">
      <h2>Wearful</h2>
    </div>

    <div class="footer-links">
      <a href="#">Home</a>
      <a href="#">Catalogo</a>
      <a href="#">Chi Siamo</a>
      <a href="#">Contatti</a>
      <a href="#">Domande</a>
    </div>

    <div class="footer-social">
      <a href="#"><i class="fa-brands fa-facebook"></i></a>
      <a href="#"><i class="fa-brands fa-instagram"></i></a>
      <a href="#"><i class="fa-brands fa-x-twitter"></i></a>
    </div>
  </div>

  <div class="footer-bottom">
    <p>&copy;  Wearful <%= Calendar.getInstance().get(Calendar.YEAR) %>. Tutti i diritti riservati.</p>
  </div>
</footer>

</body>
</html>