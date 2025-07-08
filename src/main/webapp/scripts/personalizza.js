document.addEventListener('DOMContentLoaded', () => {
    const fileUploadBtn = document.getElementById('fileUploadBtn');
    const customImagePreview = document.getElementById('customImagePreview');

    if (fileUploadBtn && customImagePreview) {
    fileUploadBtn.addEventListener('change', (event) => {
    const file = event.target.files[0];

    if (file) {
    if (file.type.startsWith('image/')) {
    const reader = new FileReader();

    reader.onload = (e) => {
    const img = new Image();
    img.onload = () => {
    const canvas = document.createElement('canvas');
    const targetWidth = 256;
    const targetHeight= 320;
    canvas.width = targetWidth;
    canvas.height = targetHeight;
    const ctx = canvas.getContext('2d');

    ctx.drawImage(img, 0, 0, targetWidth, targetHeight);

    customImagePreview.src = canvas.toDataURL('image/png');
    customImagePreview.style.display = 'block';
};
    img.src = e.target.result;
};

    reader.readAsDataURL(file);
} else {
    alert('Seleziona solo file immagine!');
    fileUploadBtn.value = '';
    customImagePreview.style.display = 'none';
    customImagePreview.src = '';
}
} else {
    customImagePreview.style.display = 'none';
    customImagePreview.src = '';
}
});
}
});
