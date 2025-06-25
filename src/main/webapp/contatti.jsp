<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contatti - Wearful</title>
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/small_logo.png">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/common.css?v=1.0">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/stileheader.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/stilefooter.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="https://kit.fontawesome.com/4c2f47ebbf.js" crossorigin="anonymous"></script>
</head>
<body>

<jsp:include page="header.jsp" />

<main>
    <section id="menu" class="menu-section">

        <h1>Contattaci!</h1>
        <p>
            Hai domande, suggerimenti o vuoi semplicemente salutarci? Siamo qui per te!
            <br>Trovaci sui nostri canali social o usa i contatti rapidi. <br>
        </p>

        <div class="contact-content-single-column">
            <div class="social-info-container">
                <h3>Seguici sui social!</h3>
                <p>Resta aggiornato sulle ultime novità, prodotti e offerte esclusive.</p>
                <div class="social-links">
                    <a href="#" target="_blank" aria-label="Seguici su Facebook">
                        <i class="fab fa-facebook-f"></i>
                    </a>
                    <a href="#" target="_blank" aria-label="Seguici su Instagram">
                        <i class="fab fa-instagram"></i>
                    </a>
                    <a href="#" target="_blank" aria-label="Seguici su X (Twitter)">
                        <i class="fab fa-x-twitter"></i>
                    </a>
                    <a href="#" target="_blank" aria-label="Seguici su Pinterest">
                        <i class="fab fa-pinterest"></i>
                    </a>
                </div>

                <h4>Contatti rapidi</h4>
                <div class="contact-details">
                    <p><i class="material-icons">email</i> info@wearful.com</p>
                    <p><i class="material-icons">phone</i> +39 0123 456789</p>
                    <p><i class="material-icons">location_on</i> Via Wearful 10, 00100 Roma RM</p>
                </div>
            </div>
        </div>
    </section>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>