document.addEventListener('DOMContentLoaded', function() {
    const colorTextInput = document.getElementById('productColore');
    const colorPicker = document.getElementById('colorPicker');
    const productImgFile = document.getElementById('productImgFile');

    // Nuovi elementi per l'anteprima e il contagocce
    const imagePreviewContainer = document.getElementById('imagePreviewContainer');
    const colorDropperCanvas = document.getElementById('colorDropperCanvas');
    const ctx = colorDropperCanvas.getContext('2d');
    const currentColorHexSpan = document.getElementById('currentColorHex');
    const currentColorSwatch = document.getElementById('currentColorSwatch');

    let currentImage = null; // Memorizzerà l'oggetto Image caricato per il canvas

    // --- Sincronizzazione esistente tra input testo colore e color picker ---
    colorTextInput.addEventListener('input', function() {
        let hexValue = colorTextInput.value;
        if (hexValue.startsWith('#') && hexValue.length === 7) {
            colorPicker.value = hexValue;
            updateColorDisplay(hexValue);
        } else if (hexValue.length === 6 && !hexValue.startsWith('#')) {
            // Se l'utente digita 6 cifre senza #, aggiungiamo noi il #
            hexValue = '#' + hexValue;
            colorTextInput.value = hexValue; // Aggiorna l'input testuale
            colorPicker.value = hexValue;
            updateColorDisplay(hexValue);
        } else {
            // Potresti voler gestire altri formati o feedback per l'utente
            // Per ora, aggiorniamo solo il swatch se possibile, altrimenti lo resettiamo
            try {
                // Prova a settare il colore, se non valido il browser lo ignorerà
                colorPicker.value = "#000000"; // Resetta a un valore base se non valido
                updateColorDisplay("#------"); // Mostra un placeholder
            } catch (e) {
                // Ignore errors
            }
        }
    });

    colorPicker.addEventListener('input', function() {
        const hexColor = colorPicker.value;
        colorTextInput.value = hexColor;
        updateColorDisplay(hexColor);
    });

    // Inizializza il color picker e l'anteprima al caricamento della pagina
    const initialColor = colorTextInput.value && colorTextInput.value.startsWith('#') && colorTextInput.value.length === 7
        ? colorTextInput.value : "#ffffff";
    colorPicker.value = initialColor;
    updateColorDisplay(initialColor);


    // --- Gestione del caricamento dell'immagine e disegno sul canvas ---
    productImgFile.addEventListener('change', function() {
        const file = this.files[0];
        if (!file) {
            imagePreviewContainer.style.display = 'none'; // Nascondi se nessun file
            currentImage = null;
            return;
        }

        const reader = new FileReader();

        reader.onload = function(e) {
            const img = new Image();
            img.onload = function() {
                currentImage = img; // Salva l'immagine caricata per il contagocce

                // Mostra il contenitore dell'anteprima
                imagePreviewContainer.style.display = 'block';

                // Imposta le dimensioni del canvas, ridimensionando l'immagine per l'anteprima
                const maxWidth = 320; // Larghezza massima per l'anteprima nel layout
                const maxHeight = 320; // Altezza massima per l'anteprima nel layout

                let width = img.width;
                let height = img.height;

                // Calcola le nuove dimensioni mantenendo l'aspect ratio
                if (width > maxWidth) {
                    height = height * (maxWidth / width);
                    width = maxWidth;
                }
                if (height > maxHeight) {
                    width = width * (maxHeight / height);
                    height = maxHeight;
                }

                colorDropperCanvas.width = width;
                colorDropperCanvas.height = height;

                // Disegna l'immagine sul canvas ridimensionata
                ctx.drawImage(img, 0, 0, width, height);
            };
            img.onerror = function() {
                console.error("Errore nel caricamento dell'immagine.");
                alert("Errore nel caricamento dell'immagine. Assicurati che sia un file immagine valido.");
                imagePreviewContainer.style.display = 'none';
                currentImage = null;
            };
            img.src = e.target.result;
        };

        reader.onerror = function() {
            console.error("Errore nella lettura del file.");
            alert("Errore nella lettura del file immagine.");
            imagePreviewContainer.style.display = 'none';
            currentImage = null;
        };

        reader.readAsDataURL(file); // Legge il file come URL di dati (Base64)
    });

    // --- Funzionalità Contagocce sul Canvas ---
    colorDropperCanvas.addEventListener('click', function(event) {
        if (!currentImage) {
            return; // Nessuna immagine caricata sul canvas
        }

        // Ottieni le coordinate del click relative al canvas
        const rect = colorDropperCanvas.getBoundingClientRect();
        // Calcola lo scaling se il canvas è ridimensionato via CSS
        const scaleX = colorDropperCanvas.width / rect.width;
        const scaleY = colorDropperCanvas.height / rect.height;

        const x = Math.floor((event.clientX - rect.left) * scaleX);
        const y = Math.floor((event.clientY - rect.top) * scaleY);

        // Ottieni i dati del pixel al punto cliccato
        try {
            // ImageData richiede che l'immagine sia "pulita" o dello stesso dominio
            const imageData = ctx.getImageData(x, y, 1, 1); // Ottieni solo 1 pixel
            const pixel = imageData.data; // Array [R, G, B, A]

            const r = pixel[0];
            const g = pixel[1];
            const b = pixel[2];

            // Converti RGB in formato esadecimale (HEX)
            const hexColor = '#' +
                ('0' + r.toString(16)).slice(-2) +
                ('0' + g.toString(16)).slice(-2) +
                ('0' + b.toString(16)).slice(-2);

            // Imposta il color picker e l'input di testo
            colorPicker.value = hexColor;
            colorTextInput.value = hexColor;

            // Aggiorna anche la visualizzazione del colore corrente
            updateColorDisplay(hexColor);

        } catch (e) {
            console.error("Impossibile leggere il pixel:", e);
            alert("Impossibile leggere il colore da questa posizione dell'immagine. Potrebbe essere un problema di sicurezza (CORS) se l'immagine proviene da un'altra fonte.");
        }
    });

    // Funzione per aggiornare la visualizzazione del colore corrente
    function updateColorDisplay(hexColor) {
        if (hexColor.startsWith('#') && hexColor.length === 7) {
            currentColorHexSpan.textContent = hexColor.toUpperCase();
            currentColorSwatch.style.backgroundColor = hexColor;
        } else {
            currentColorHexSpan.textContent = "#------";
            currentColorSwatch.style.backgroundColor = "transparent"; // O un colore neutro
        }
    }
});