document.addEventListener('DOMContentLoaded', () => {
    const addBtn = document.getElementById('addAddressBtn');
    const formSection = document.getElementById('form-section');
    const errorDiv = document.getElementById('form-error');
    const submitBtn = document.getElementById('submit-button');

    if (formSection) {
        formSection.style.display = 'none';
    }

    window.mostraForm = (id = '', destinatario = '', via = '', citta = '', cap = '', altro = '') => {
        if (formSection) {
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

            submitBtn.value = id ? 'Salva Modifiche' : 'Aggiungi Indirizzo';

            checkForm();
            formSection.scrollIntoView({ behavior: 'smooth' });
        }
    };

    if (addBtn) {
        addBtn.addEventListener('click', () => {
            mostraForm();
        });
    }

    const requiredFields = ['destinatario', 'via', 'citta', 'cap'].map(id => document.getElementById(id));

    function checkForm() {
        let errorMessage = '';
        const isFormEmpty = requiredFields.some(field => field.value.trim() === '');
        const capValue = document.getElementById('cap').value.trim();

        if (isFormEmpty) {
            errorMessage = 'Tutti i campi obbligatori devono essere compilati.';
        } else if (!/^\d{5}$/.test(capValue)) {
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

    if (formSection) {
        formSection.addEventListener('input', checkForm);
        formSection.addEventListener('submit', (event) => {
            checkForm();
            if (submitBtn.disabled) {
                event.preventDefault();
            }
        });
    }
});