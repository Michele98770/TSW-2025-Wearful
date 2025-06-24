<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi siamo - Wearful</title>
    <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/small_logo.png">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/stileheader.css?v=1.0">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/stilefooter.css?v=1.0">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/stylesheets/common.css?v=1.0.1"> <%-- Changed base.css to common.css --%>
    <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="https://kit.fontawesome.com/4c2f47ebbf.js" crossorigin="anonymous"></script>
</head>
<body>

<%-- INIZIO HEADER --%>
<jsp:include page="header.jsp" />
<%-- FINE HEADER --%>

<main>
    <section id="menu">
        <h1>Chi siamo?</h1>
        <p>
            Siamo un team di appassionati di <b>videogiochi</b>, <b>film</b>, <b>fumetti</b> e <b>comics</b>, che ha fondato Wearful per condividere la nostra passione!
            <br><br>
            La nostra missione è offrire un punto di riferimento per chi, come noi, desidera esprimere la propria identità nerd con stile e originalità.
            <br><br>
            Proponiamo una selezione curata di <b>magliette</b>, <b>felpe</b> e <b>cappelli</b> a tema nerd, tutti caratterizzati da design originali e una qualità manifatturiera superiore, e
            ogni capo è concepito per essere un'affermazione della vostra passione, un modo per distinguervi e celebrare il vostro legame con gli universi che amate.
            <br><br>
            Siamo in continua ricerca di nuove idee e tendenze, e il vostro feedback è prezioso per noi nel modellare la nostra offerta!
        </p>

        <h2>Perché scegliere noi?</h2>

        <ul>
            <li>Design <b>originali</b> e di <b>alta qualità</b></li>
            <li>Prodotti perfetti per esprimere la tua passione per il mondo nerd</li>
            <li>Ampia scelta di prodotti</li>
            <li><b>Spedizione veloce</b> e <b>gratuita</b> in tutta Italia</li>
            <li>Servizio clienti sempre disponibile</li>
        </ul>

    </section>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>

