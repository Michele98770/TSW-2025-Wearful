<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Calendar" %>
<footer class="footer">
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
    <p>&copy; <%= Calendar.getInstance().get(Calendar.YEAR) %> Wearful. Tutti i diritti riservati.</p>
  </div>
</footer>