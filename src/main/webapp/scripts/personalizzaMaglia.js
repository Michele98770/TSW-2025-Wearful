document.addEventListener('DOMContentLoaded', () => {
    const tshirtImage = document.getElementById('tshirtImage');
    const customOverlayImage = document.getElementById('customOverlayImage');
    const imageUploader = document.getElementById('imageUploader');
    const colorOptionsContainer = document.querySelector('.color-options');
    const selectedColorDisplay = document.getElementById('selectedColorDisplay');
    const selectedProductIdInput = document.getElementById('selectedProductId');

    if (colorOptionsContainer) {
        colorOptionsContainer.addEventListener('click', (event) => {
            const target = event.target;
            if (target.classList.contains('color-option')) {
                const selectedColor = target.dataset.color;
                const productVariant = productsData[selectedColor];

                if (productVariant) {
                    tshirtImage.src = productVariant.imgPath;
                    selectedProductIdInput.value = productVariant.id;
                    selectedColorDisplay.textContent = selectedColor;

                    document.querySelectorAll('.color-option').forEach(option => {
                        option.classList.remove('selected');
                    });
                    target.classList.add('selected');
                }
            }
        });
    }

    if (imageUploader) {
        imageUploader.addEventListener('change', (event) => {
            const file = event.target.files[0];

            if (file) {
                if (!file.type.startsWith('image/')) {
                    alert('Per favore, seleziona un file immagine (es. PNG, JPG).');
                    imageUploader.value = '';
                    return;
                }

                const reader = new FileReader();

                reader.onload = (e) => {
                    customOverlayImage.src = e.target.result;
                    customOverlayImage.style.display = 'block';
                };

                reader.readAsDataURL(file);
            } else {
                customOverlayImage.style.display = 'none';
                customOverlayImage.src = '';
            }
        });
    }
});