document.querySelectorAll('input[type="number"][data-product-id]').forEach(input => {
    input.addEventListener('change', function() {
        const productId = this.dataset.productId;
        const productSize = this.dataset.productSize;
        const newQuantity = this.value;


        const body = `action=aggiornaQuantita&idProdotto=${productId}&taglia=${encodeURIComponent(productSize)}&quantita=${newQuantity}`;

        fetch('CarrelloServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: body
        })
            .then(response => {
                if (!response.ok) {

                    throw new Error('Errore di rete o del server.');
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    location.reload();
                } else {

                    alert('Errore: ' + data.message);
                    location.reload();
                }
            })
            .catch(error => {
                console.error('Errore durante la richiesta fetch:', error);
            });
    });
});