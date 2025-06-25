document.addEventListener('DOMContentLoaded', function() {
    const productNameInput = document.getElementById('productName');
    const productDescriptionInput = document.getElementById('productDescription');
    const productTagliaSelect = document.getElementById('productTaglia');
    const productColoreInput = document.getElementById('productColore');
    const productCodiceColoreInput = document.getElementById('productCodiceColore');
    const productCategoriaInput = document.getElementById('productCategoria');
    const productPrezzoInput = document.getElementById('productPrezzo');
    const productIvaSelect = document.getElementById('productIVA');
    const productDisponibilitaInput = document.getElementById('productDisponibilita');
    const personalizzabileYesRadio = document.getElementById('personalizzabileYes');
    const personalizzabileNoRadio = document.getElementById('personalizzabileNo');
    const productImgFile = document.getElementById('productImgFile');
    const colorPicker = document.getElementById('colorPicker');

    const imagePreviewContainer = document.getElementById('imagePreviewContainer');
    const colorDropperCanvas = document.getElementById('colorDropperCanvas');
    const ctx = colorDropperCanvas.getContext('2d');
    const currentColorHexSpan = document.getElementById('currentColorHex');
    const currentColorSwatch = document.getElementById('currentColorSwatch');

    let currentImage = null;

    const productForm = document.querySelector('.admin-registration-form');
    const submitButton = productForm.querySelector('.admin-submit-button');

    productCodiceColoreInput.addEventListener('input', function() {
        let hexValue = productCodiceColoreInput.value.trim();

        if (!hexValue.startsWith('#') && hexValue.length <= 6) {
            hexValue = '#' + hexValue;
        }

        if (hexValue.match(/^#([0-9A-Fa-f]{6})$/)) {
            colorPicker.value = hexValue;
            updateColorDisplay(hexValue);
        } else {
            colorPicker.value = "#000000";
            updateColorDisplay("#------");
        }
        toggleSubmitButton();
    });

    colorPicker.addEventListener('input', function() {
        const hexColor = colorPicker.value;
        productCodiceColoreInput.value = hexColor;
        updateColorDisplay(hexColor);
        toggleSubmitButton();
    });

    const initialCodiceColore = productCodiceColoreInput.value.trim();
    if (initialCodiceColore.match(/^#([0-9A-Fa-f]{6})$/)) {
        colorPicker.value = initialCodiceColore;
        updateColorDisplay(initialCodiceColore);
    } else {
        productCodiceColoreInput.value = "#000000";
        colorPicker.value = "#000000";
        updateColorDisplay("#000000");
    }

    productImgFile.addEventListener('change', function() {
        const file = this.files[0];
        if (!file) {
            imagePreviewContainer.style.display = 'none';
            currentImage = null;
            toggleSubmitButton();
            return;
        }

        const reader = new FileReader();

        reader.onload = function(e) {
            const img = new Image();
            img.onload = function() {
                currentImage = img;
                imagePreviewContainer.style.display = 'block';

                const maxWidth = 320;
                const maxHeight = 320;

                let width = img.width;
                let height = img.height;

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
                ctx.drawImage(img, 0, 0, width, height);
                toggleSubmitButton();
            };
            img.onerror = function() {
                console.error("Errore nel caricamento dell'immagine.");
                alert("Errore nel caricamento dell'immagine. Assicurati che sia un file immagine valido.");
                imagePreviewContainer.style.display = 'none';
                currentImage = null;
                toggleSubmitButton();
            };
            img.src = e.target.result;
        };

        reader.onerror = function() {
            console.error("Errore nella lettura del file.");
            alert("Errore nella lettura del file immagine.");
            imagePreviewContainer.style.display = 'none';
            currentImage = null;
            toggleSubmitButton();
        };

        reader.readAsDataURL(file);
    });

    colorDropperCanvas.addEventListener('click', function(event) {
        if (!currentImage) {
            return;
        }

        const rect = colorDropperCanvas.getBoundingClientRect();
        const scaleX = colorDropperCanvas.width / rect.width;
        const scaleY = colorDropperCanvas.height / rect.height;

        const x = Math.floor((event.clientX - rect.left) * scaleX);
        const y = Math.floor((event.clientY - rect.top) * scaleY);

        try {
            const imageData = ctx.getImageData(x, y, 1, 1);
            const pixel = imageData.data;

            const r = pixel[0];
            const g = pixel[1];
            const b = pixel[2];

            const hexColor = '#' +
                ('0' + r.toString(16)).slice(-2) +
                ('0' + g.toString(16)).slice(-2) +
                ('0' + b.toString(16)).slice(-2);

            productCodiceColoreInput.value = hexColor;
            colorPicker.value = hexColor;
            updateColorDisplay(hexColor);
            toggleSubmitButton();
        } catch (e) {
            console.error("Impossibile leggere il pixel:", e);
            alert("Impossibile leggere il colore da questa posizione dell'immagine. Potrebbe essere un problema di sicurezza (CORS) se l'immagine proviene da un'altra fonte.");
        }
    });

    function updateColorDisplay(hexColor) {
        if (hexColor.match(/^#([0-9A-Fa-f]{6})$/)) {
            currentColorHexSpan.textContent = hexColor.toUpperCase();
            currentColorSwatch.style.backgroundColor = hexColor;
        } else {
            currentColorHexSpan.textContent = "#------";
            currentColorSwatch.style.backgroundColor = "transparent";
        }
    }

    function validateForm() {
        let isValid = true;
        const errors = [];

        const groupNameInput = document.getElementById('groupName');
        if (!groupNameInput.readOnly && groupNameInput.value.trim() === '') {
            errors.push("Il campo 'Nome Linea di prodotti' è obbligatorio.");
            isValid = false;
        }

        if (productNameInput.value.trim() === '') {
            errors.push("Il campo 'Nome Prodotto' è obbligatorio.");
            isValid = false;
        }

        if (productDescriptionInput.value.trim() === '') {
            errors.push("Il campo 'Descrizione' è obbligatorio.");
            isValid = false;
        }

        if (productTagliaSelect.value === '') {
            errors.push("Selezionare una 'Taglia'.");
            isValid = false;
        }

        if (productColoreInput.value.trim() === '') {
            errors.push("Il campo 'Nome Colore' è obbligatorio.");
            isValid = false;
        }

        if (!productCodiceColoreInput.value.trim().match(/^#([0-9A-Fa-f]{6})$/)) {
            errors.push("Il campo 'Colore' deve essere un codice esadecimale valido (es. #RRGGBB).");
            isValid = false;
        }

        if (productCategoriaInput.value === '') {
            errors.push("Selezionare una 'Categoria'.");
            isValid = false;
        }

        const prezzo = parseFloat(productPrezzoInput.value);
        if (isNaN(prezzo) || prezzo <= 0) {
            errors.push("Il campo 'Prezzo' deve essere un numero positivo.");
            isValid = false;
        }

        if (productIvaSelect.value === '') {
            errors.push("Selezionare un'aliquota 'IVA'.");
            isValid = false;
        }

        const disponibilita = parseInt(productDisponibilitaInput.value, 10);
        if (isNaN(disponibilita) || disponibilita < 0) {
            errors.push("Il campo 'Disponibilità' deve essere un numero intero non negativo.");
            isValid = false;
        }

        if (!personalizzabileYesRadio.checked && !personalizzabileNoRadio.checked) {
            errors.push("Selezionare se il prodotto è 'Personalizzabile'.");
            isValid = false;
        }

        if (productImgFile.files.length === 0) {
            errors.push("Caricare un'immagine per il prodotto.");
            isValid = false;
        }

        return isValid;
    }

    function displayValidationErrors(errors) {
    }

    function toggleSubmitButton() {
        if (validateForm()) {
            submitButton.disabled = false;
            submitButton.style.opacity = '1';
            submitButton.style.cursor = 'pointer';
        } else {
            submitButton.disabled = true;
            submitButton.style.opacity = '0.5';
            submitButton.style.cursor = 'not-allowed';
        }
    }

    const formElements = productForm.querySelectorAll('input, select, textarea');
    formElements.forEach(element => {
        if (element.type === 'text' || element.type === 'number' || element.type === 'color' || element.tagName === 'TEXTAREA') {
            element.addEventListener('input', toggleSubmitButton);
        } else if (element.tagName === 'SELECT' || element.type === 'file' || element.type === 'radio') {
            element.addEventListener('change', toggleSubmitButton);
        }
    });

    productForm.addEventListener('submit', function(event) {
        if (!validateForm()) {
            event.preventDefault();
        }
    });

    toggleSubmitButton();
});