document.addEventListener('DOMContentLoaded', () => {
    const grid = document.getElementById('product-grid');
    const panelTemplate = document.getElementById('customization-panel-template');

    let currentPanel = null;
    let currentProductGroupData = null;
    const sizeOrder = { 'XXS': 1, 'XS': 2, 'S': 3, 'M': 4, 'L': 5, 'XL': 6, 'XXL': 7 };

    function sortSizes(sizes) {
        return sizes.sort((a, b) => (sizeOrder[a.toUpperCase()] || 99) - (sizeOrder[b.toUpperCase()] || 99));
    }

    function closePanel() {
        if (currentPanel) {
            currentPanel.remove();
            currentPanel = null;
        }
        document.querySelector('.product-card.active')?.classList.remove('active');
    }

    function showPanel(clickedCard) {
        closePanel();

        clickedCard.classList.add('active');

        const groupName = clickedCard.dataset.groupName;
        currentProductGroupData = allProductsData[groupName];
        if (!currentProductGroupData) return;

        const panelWrapper = document.createElement('div');
        panelWrapper.className = 'customization-panel-wrapper';
        panelWrapper.innerHTML = panelTemplate.innerHTML;
        currentPanel = panelWrapper;

        const itemsPerRow = Math.floor(grid.clientWidth / (clickedCard.offsetWidth + 20));
        const cardIndex = Array.from(grid.querySelectorAll('.product-card')).indexOf(clickedCard);
        const endOfRowIndex = Math.ceil((cardIndex + 1) / itemsPerRow) * itemsPerRow;
        const insertBeforeNode = grid.children[endOfRowIndex];

        grid.insertBefore(panelWrapper, insertBeforeNode);

        populatePanel(panelWrapper, groupName);
        panelWrapper.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    function populatePanel(panel, groupName) {
        const firstVariant = Object.values(Object.values(currentProductGroupData)[0])[0];

        panel.querySelector('.productName').textContent = firstVariant.nome;
        panel.querySelector('.product-description').textContent = firstVariant.descrizione;

        const colorContainer = panel.querySelector('.color-options');
        const sizeContainer = panel.querySelector('.size-options');

        const colors = Object.keys(currentProductGroupData);
        colors.forEach((color, index) => {
            const representativeVariant = Object.values(currentProductGroupData[color])[0];
            const colorDiv = document.createElement('div');
            colorDiv.className = 'color-option';
            colorDiv.dataset.color = color;
            colorDiv.title = color;
            colorDiv.style.backgroundColor = representativeVariant.codiceColore;
            if (index === 0) colorDiv.classList.add('selected');
            colorContainer.appendChild(colorDiv);
        });

        renderSizeOptions(panel, colors[0]);

        panel.querySelector('.close-panel-btn').addEventListener('click', closePanel);

        colorContainer.addEventListener('click', (e) => {
            const colorDiv = e.target.closest('.color-option');
            if (colorDiv && !colorDiv.classList.contains('selected')) {
                colorContainer.querySelector('.selected').classList.remove('selected');
                colorDiv.classList.add('selected');
                renderSizeOptions(panel, colorDiv.dataset.color);
            }
        });

        sizeContainer.addEventListener('click', (e) => {
            const sizeDiv = e.target.closest('.size-option');
            if (sizeDiv && !sizeDiv.classList.contains('unavailable') && !sizeDiv.classList.contains('selected')) {
                sizeContainer.querySelector('.selected').classList.remove('selected');
                sizeDiv.classList.add('selected');
                updateVariantDisplay(panel);
            }
        });

        panel.querySelector('.imageUploader').addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file && file.type.startsWith('image/')) {
                const reader = new FileReader();
                reader.onload = (event) => {
                    panel.querySelector('.customOverlayImage').src = event.target.result;
                    panel.querySelector('.customOverlayImage').style.display = 'block';
                };
                reader.readAsDataURL(file);
            }
        });
    }

    function renderSizeOptions(panel, color) {
        const sizeContainer = panel.querySelector('.size-options');
        sizeContainer.innerHTML = '';
        const sizes = currentProductGroupData[color] ? Object.keys(currentProductGroupData[color]) : [];
        const sortedSizes = sortSizes(sizes);

        let preselectedSize = sortedSizes.find(s => currentProductGroupData[color][s]?.disponibilita > 0) || sortedSizes[0];

        sortedSizes.forEach(size => {
            const variant = currentProductGroupData[color][size];
            const sizeDiv = document.createElement('div');
            sizeDiv.className = 'size-option';
            sizeDiv.dataset.size = size;
            sizeDiv.textContent = size;
            if (!variant || variant.disponibilita <= 0) sizeDiv.classList.add('unavailable');
            if (size === preselectedSize) sizeDiv.classList.add('selected');
            sizeContainer.appendChild(sizeDiv);
        });
        updateVariantDisplay(panel);
    }

    function updateVariantDisplay(panel) {
        const color = panel.querySelector('.color-option.selected')?.dataset.color;
        const size = panel.querySelector('.size-option.selected')?.dataset.size;

        if (!color || !size) return;

        const variant = currentProductGroupData[color][size];

        panel.querySelector('.tshirtImage').src = variant.imgPath;
        panel.querySelector('.product-price').textContent = `€ ${variant.prezzoFinale.toFixed(2).replace('.', ',')}`;
        panel.querySelector('.selectedProductId').value = variant.id;
        panel.querySelector('.selectedColorDisplay').textContent = color;

        const availabilityStatus = panel.querySelector('.availability-status');
        const quantityInput = panel.querySelector('.quantity');
        const addToCartBtn = panel.querySelector('.add-to-cart-btn');

        const availability = variant.disponibilita;
        quantityInput.max = availability;
        if (availability > 0) {
            availabilityStatus.textContent = `Disponibili: ${availability}`;
            availabilityStatus.classList.remove('out-of-stock');
            quantityInput.disabled = false;
            addToCartBtn.disabled = false;
            if (parseInt(quantityInput.value) < 1) quantityInput.value = 1;
        } else {
            availabilityStatus.textContent = 'Esaurito';
            availabilityStatus.classList.add('out-of-stock');
            quantityInput.disabled = true;
            addToCartBtn.disabled = true;
            quantityInput.value = 0;
        }
    }

    grid.addEventListener('click', (event) => {
        const card = event.target.closest('.product-card');
        if (card) {
            showPanel(card);
        }
    });
});