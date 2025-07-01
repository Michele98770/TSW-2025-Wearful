<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="./img/small_logo.png">
    <title>Domande Frequenti - Wearful</title>
</head>
<body>

<jsp:include page="header.jsp" />

<main>
    <section id="menu" class="menu-section">
        <h1>Domande Frequenti</h1>
        <p>Queste sono le risposte alle domande più frequenti che ci vengono poste!
            <br>Per ulteriori informazioni, consulta la sezione <a href="<%= request.getContextPath() %>/contatti.jsp">Contatti</a>
        </p>

        <div class="faq-item">
            <button class="faq-question">
                <span>Quali sono i tempi di spedizione?</span>
                <i class="material-icons expand-icon">expand_more</i>
            </button>
            <div class="faq-answer">
                <p>Generalmente, gli ordini vengono elaborati in 1-2 giorni lavorativi e spediti entro 3-5 giorni lavorativi per le consegne in Italia. Riceverai un'email con il tracking non appena il tuo ordine sarà spedito.</p>
            </div>
        </div>

        <div class="faq-item">
            <button class="faq-question">
                <span>Accettate resi o cambi?</span>
                <i class="material-icons expand-icon">expand_more</i>
            </button>
            <div class="faq-answer">
                <p>Sì, accettiamo resi e cambi entro 14 giorni dalla ricezione del prodotto, purché l'articolo sia nelle sue condizioni originali, non indossato, non lavato e munito di etichetta originale, specificando le motivazioni del reso o del cambio.</p>
            </div>
        </div>

        <div class="faq-item">
            <button class="faq-question">
                <span>Posso personalizzare un design?</span>
                <i class="material-icons expand-icon">expand_more</i>
            </button>
            <div class="faq-answer">
                <p>Certamente! Cliccando sul banner presente nella schermata Home, è possibile accedere alla schermata per caricare immagini da stampare sui prodotti.
                    <br><u>Nota bene</u>: se le immagini caricate non rispettano le nostre policy, l'ordine verrà annullato!
                </p>
            </div>
        </div>

        <div class="faq-item">
            <button class="faq-question">
                <span>Come posso lavare le mie magliette?</span>
                <i class="material-icons expand-icon">expand_more</i>
            </button>
            <div class="faq-answer">
                <p>Per preservare al meglio la stampa e la qualità del tessuto, consigliamo di lavare le magliette a rovescio, in acqua fredda (max 30°C), con ciclo delicato. Non usare candeggina e asciugare all'aria. Stirare a rovescio e a bassa temperatura.</p>
            </div>
        </div>

        <div class="faq-item">
            <button class="faq-question">
                <span>I vostri prodotti sono unisex?</span>
                <i class="material-icons expand-icon">expand_more</i>
            </button>
            <div class="faq-answer">
                <p>La maggior parte delle nostre magliette e felpe sono progettate per essere unisex, con una vestibilità comoda e versatile.</p>
            </div>
        </div>

        <script src="./scripts/faq.js"></script>

    </section>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>