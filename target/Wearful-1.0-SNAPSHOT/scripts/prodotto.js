const productImage = document.getElementById('productImage');
const productName = document.getElementById('productName');
const productDescription = document.getElementById('productDescription');
const productPrice = document.getElementById('productPrice');
const availabilityStatus = document.getElementById('availabilityStatus');
const selectedProductIdInput = document.getElementById('selectedProductId');
const quantityInput = document.getElementById('quantity');
const addToCartBtn = document.querySelector('.add-to-cart-btn');

const colorOptions = document.querySelectorAll('.color-option');
const sizeOptionsContainer = document.querySelector('.size-options');
const selectedColorDisplay = document.getElementById('selectedColorDisplay');
const selectedSizeDisplay = document.getElementById('selectedSizeDisplay');

let currentSelectedColor;
let currentSelectedSize;

const sizeOrder = {
    'XXS': 1, 'XS': 2, 'S': 3, 'M': 4, 'L': 5, 'XL': 6, 'XXL': 7,
};

function sortSizes(sizes) {
    return sizes.sort((a, b) => {
        const valA = sizeOrder[a.toUpperCase()] || 99;
        const valB = sizeOrder[b.toUpperCase()] || 99;
        if (valA === valB) {
            return a.localeCompare(b);
        }
        return valA - valB;
    });
}

function updateProductDetails() {
    if (!currentSelectedColor || !currentSelectedSize) {
        availabilityStatus.textContent = 'Seleziona colore e taglia.';
        availabilityStatus.classList.add('out-of-stock');
        quantityInput.disabled = true;
        addToCartBtn.disabled = true;
        selectedProductIdInput.value = '';
        quantityInput.value = 0;
        productImage.src = '';
        productPrice.textContent = '';
        selectedColorDisplay.textContent = currentSelectedColor || 'N/D';
        selectedSizeDisplay.textContent = currentSelectedSize || 'N/D';
        return;
    }

    const selectedProduct = productsData[currentSelectedColor] ? productsData[currentSelectedColor][currentSelectedSize] : null;

    if (selectedProduct) {
        productImage.src = selectedProduct.imgPath;
        productImage.alt = selectedProduct.nome;

        productPrice.textContent = `€ ${new Intl.NumberFormat('it-IT', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(selectedProduct.prezzo)}`;

        selectedProductIdInput.value = selectedProduct.id;

        const availability = selectedProduct.disponibilita;
        quantityInput.max = availability;

        if (parseInt(quantityInput.value) > availability || parseInt(quantityInput.value) < 1 || isNaN(parseInt(quantityInput.value))) {
            quantityInput.value = Math.min(1, availability);
        }

        if (availability <= 0) {
            availabilityStatus.textContent = 'Esaurito';
            availabilityStatus.classList.add('out-of-stock');
            quantityInput.disabled = true;
            addToCartBtn.disabled = true;
            quantityInput.value = 0;
        } else {
            availabilityStatus.textContent = `Disponibili: ${availability}`;
            availabilityStatus.classList.remove('out-of-stock');
            quantityInput.disabled = false;
            addToCartBtn.disabled = false;
        }
    } else {
        availabilityStatus.textContent = 'Non disponibile con questa combinazione.';
        availabilityStatus.classList.add('out-of-stock');
        quantityInput.disabled = true;
        addToCartBtn.disabled = true;
        selectedProductIdInput.value = '';
        quantityInput.value = 0;
        productImage.src = '';
        productPrice.textContent = '';
    }
    selectedColorDisplay.textContent = currentSelectedColor;
    selectedSizeDisplay.textContent = currentSelectedSize || 'N/D';
}

function updateSizeOptions() {
    sizeOptionsContainer.innerHTML = '';

    const sizesForCurrentColor = productsData[currentSelectedColor] ? Object.keys(productsData[currentSelectedColor]) : [];

    const sortedSizes = sortSizes(sizesForCurrentColor);

    let newSizeToSelect = null;

    if (currentSelectedSize &&
        productsData[currentSelectedColor] &&
        productsData[currentSelectedColor][currentSelectedSize] &&
        productsData[currentSelectedColor][currentSelectedSize].disponibilita > 0) {
        newSizeToSelect = currentSelectedSize;
    } else {
        for (const size of sortedSizes) {
            const productVariant = productsData[currentSelectedColor][size];
            if (productVariant && productVariant.disponibilita > 0) {
                newSizeToSelect = size;
                break;
            }
        }
        if (!newSizeToSelect && sortedSizes.length > 0) {
            newSizeToSelect = sortedSizes[0];
        }
    }

    currentSelectedSize = newSizeToSelect;

    sortedSizes.forEach(size => {
        const productVariant = productsData[currentSelectedColor][size];
        const isAvailable = productVariant && productVariant.disponibilita > 0;
        const sizeDiv = document.createElement('div');
        sizeDiv.classList.add('size-option');
        sizeDiv.dataset.size = size;
        sizeDiv.textContent = size;

        if (!isAvailable) {
            sizeDiv.classList.add('unavailable');
        }

        if (size === currentSelectedSize) {
            sizeDiv.classList.add('selected');
        }

        sizeDiv.addEventListener('click', () => {
            if (!sizeDiv.classList.contains('unavailable') || sortedSizes.every(s => productsData[currentSelectedColor][s] && productsData[currentSelectedColor][s].disponibilita <= 0)) {
                document.querySelectorAll('.size-option').forEach(opt => opt.classList.remove('selected'));
                sizeDiv.classList.add('selected');
                currentSelectedSize = sizeDiv.dataset.size;
                updateProductDetails();
            }
        });
        sizeOptionsContainer.appendChild(sizeDiv);
    });

    updateProductDetails();
}


colorOptions.forEach(option => {
    option.addEventListener('click', () => {
        const newColor = option.dataset.color;
        if (newColor === currentSelectedColor) return;

        document.querySelectorAll('.color-option').forEach(opt => opt.classList.remove('selected'));
        option.classList.add('selected');
        currentSelectedColor = newColor;
        updateSizeOptions();
    });
});

document.addEventListener('DOMContentLoaded', () => {
    // Determina il colore inizialmente selezionato cercando l'elemento con la classe 'selected'
    const initialSelectedColorDiv = document.querySelector('.color-option.selected');
    if (initialSelectedColorDiv) {
        currentSelectedColor = initialSelectedColorDiv.dataset.color;
    } else {
        const firstColorDiv = document.querySelector('.color-option');
        if (firstColorDiv) {
            currentSelectedColor = firstColorDiv.dataset.color;
            firstColorDiv.classList.add('selected');
        }
    }

    const initialSelectedSizeDiv = document.querySelector('.size-option.selected');
    if (initialSelectedSizeDiv) {
        currentSelectedSize = initialSelectedSizeDiv.dataset.size;
    } else {
        currentSelectedSize = null;
    }

    updateSizeOptions();
});

quantityInput.addEventListener('change', () => {
    let value = parseInt(quantityInput.value);
    let max = parseInt(quantityInput.max);
    if (isNaN(value) || value < 1) {
        quantityInput.value = 1;
    } else if (value > max) {
        quantityInput.value = max;
    }
});
quantityInput.addEventListener('input', () => {
    let value = parseInt(quantityInput.value);
    let max = parseInt(quantityInput.max);
    if (value > max) {
    }
});