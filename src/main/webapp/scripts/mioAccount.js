document.addEventListener('DOMContentLoaded', () => {
    const addBtn = document.getElementById('addAddressBtn');
    const formSection = document.getElementById('form-section');

    window.mostraForm = (id = '', destinatario = '', via = '', citta = '', cap = '', altro = '') => {
        if (addBtn) {
            addBtn.style.display = 'none';
        }
        formSection.style.display = 'block';
        document.getElementById('idConsegna').value = id;
        document.getElementById('destinatario').value = destinatario;
        document.getElementById('via').value = via;
        document.getElementById('citta').value = citta;
        document.getElementById('cap').value = cap;
        document.getElementById('altro').value = altro;
        const submitBtn = document.getElementById('submit-button');
        submitBtn.value = id ? 'Salva Modifiche' : 'Aggiungi Indirizzo';
        checkForm();
    };

    if (addBtn) {
        addBtn.addEventListener('click', () => {
            mostraForm();
        });
    }

    const form = document.getElementById('form-section');
    const errorDiv = document.getElementById('form-error');
    const submitBtn = document.getElementById('submit-button');

    const fields = ['destinatario', 'via', 'citta', 'cap'].map(id => document.getElementById(id));

    function checkForm() {
        const destinatario = fields[0].value.trim();
        const via = fields[1].value.trim();
        const citta = fields[2].value.trim();
        const cap = fields[3].value.trim();

        let errorMessage = '';

        if (!destinatario || !via || !citta || !cap) {
            errorMessage = 'Tutti i campi obbligatori devono essere compilati.';
        } else if (!/^\d{5}$/.test(cap)) {
            errorMessage = 'Il CAP deve essere un numero di 5 cifre.';
        }

        if (errorMessage) {
            errorDiv.textContent = errorMessage;
            submitBtn.disabled = true;
        } else {
            errorDiv.textContent = '';
            submitBtn.disabled = false;
        }
    }

    form.addEventListener('input', checkForm);
    form.addEventListener('submit', (event) => {
        checkForm();
        if (submitBtn.disabled) {
            event.preventDefault();
        }
    });
});
