document.addEventListener('DOMContentLoaded', () => {
    const paymentCash = document.getElementById('payment_cash');
    const paymentCard = document.getElementById('payment_card');
    const cardDetails = document.getElementById('cardDetails');
    const confirmOrderBtn = document.getElementById('confirmOrderBtn');
    const checkoutForm = document.getElementById('checkoutForm');

    const cardNumberInput = document.getElementById('cardNumber');
    const expiryDateInput = document.getElementById('expiryDate');
    const cvvInput = document.getElementById('cvv');

    const addressRadios = document.querySelectorAll('input[name="selectedAddressId"]');

    function setCardRequired(isRequired) {
        if (cardNumberInput) cardNumberInput.required = isRequired;
        if (expiryDateInput) expiryDateInput.required = isRequired;
        if (cvvInput) cvvInput.required = isRequired;
    }

    function toggleCardDetails() {
        if (paymentCard && paymentCard.checked) {
            cardDetails.style.display = 'grid';
            setCardRequired(true);
        } else {
            cardDetails.style.display = 'none';
            setCardRequired(false);
        }
        validateForm();
    }

    function validateForm() {
        let isFormValid = true;

        if (cardNumberInput) {
            cardNumberInput.setCustomValidity('');
            cardNumberInput.classList.remove('is-invalid', 'is-valid');
        }
        if (expiryDateInput) {
            expiryDateInput.setCustomValidity('');
            expiryDateInput.classList.remove('is-invalid', 'is-valid');
        }
        if (cvvInput) {
            cvvInput.setCustomValidity('');
            cvvInput.classList.remove('is-invalid', 'is-valid');
        }

        let isAddressSelected = false;
        if (addressRadios.length > 0) {
            for (const radio of addressRadios) {
                if (radio.checked) {
                    isAddressSelected = true;
                    break;
                }
            }
            if (!isAddressSelected) {
                isFormValid = false;
            }
        } else {
            isFormValid = false;
        }

        if (paymentCard && paymentCard.checked) {
            let cardNumberIsValid = true;
            if (!cardNumberInput || cardNumberInput.value.length === 0) {
                cardNumberInput.setCustomValidity('Il numero di carta non può essere vuoto.');
                cardNumberInput.classList.add('is-invalid');
                cardNumberIsValid = false;
            } else if (!/^[0-9]{16}$/.test(cardNumberInput.value)) {
                cardNumberInput.setCustomValidity('Il numero di carta deve essere di 16 cifre numeriche.');
                cardNumberInput.classList.add('is-invalid');
                cardNumberIsValid = false;
            } else {
                cardNumberInput.setCustomValidity('');
                cardNumberInput.classList.add('is-valid');
            }
            if (!cardNumberIsValid) isFormValid = false;

            let expiryDateIsValid = true;
            if (!expiryDateInput || expiryDateInput.value.length === 0) {
                expiryDateInput.setCustomValidity('La data di scadenza non può essere vuota.');
                expiryDateInput.classList.add('is-invalid');
                expiryDateIsValid = false;
            } else {
                const expiryValue = expiryDateInput.value;
                const parts = expiryValue.split('/');

                if (parts.length === 2 && !isNaN(parts[0]) && !isNaN(parts[1])) {
                    const month = parseInt(parts[0], 10);
                    const year = parseInt(parts[1], 10) + 2000;

                    if (month < 1 || month > 12) {
                        expiryDateInput.setCustomValidity('Mese non valido (deve essere tra 01 e 12).');
                        expiryDateInput.classList.add('is-invalid');
                        expiryDateIsValid = false;
                    } else {
                        const currentDate = new Date();
                        const currentMonth = currentDate.getMonth() + 1;
                        const currentYear = currentDate.getFullYear();

                        if (year < currentYear || (year === currentYear && month < currentMonth)) {
                            expiryDateInput.setCustomValidity('La data di scadenza non può essere nel passato.');
                            expiryDateInput.classList.add('is-invalid');
                            expiryDateIsValid = false;
                        } else {
                            expiryDateInput.setCustomValidity('');
                            expiryDateInput.classList.add('is-valid');
                        }
                    }
                } else {
                    expiryDateInput.setCustomValidity('Formato data non valido (MM/YY).');
                    expiryDateInput.classList.add('is-invalid');
                    expiryDateIsValid = false;
                }
            }
            if (!expiryDateIsValid) isFormValid = false;

            let cvvIsValid = true;
            if (!cvvInput || cvvInput.value.length === 0) {
                cvvInput.setCustomValidity('Il CVV non può essere vuoto.');
                cvvInput.classList.add('is-invalid');
                cvvIsValid = false;
            } else if (!/^[0-9]{3,4}$/.test(cvvInput.value)) {
                cvvInput.setCustomValidity('Il CVV deve essere di 3 o 4 cifre numeriche.');
                cvvInput.classList.add('is-invalid');
                cvvIsValid = false;
            } else {
                cvvInput.setCustomValidity('');
                cvvInput.classList.add('is-valid');
            }
            if (!cvvIsValid) isFormValid = false;
        }

        if (confirmOrderBtn) {
            confirmOrderBtn.disabled = !isFormValid;
        }

        return isFormValid;
    }

    if (paymentCash) paymentCash.addEventListener('change', toggleCardDetails);
    if (paymentCard) paymentCard.addEventListener('change', toggleCardDetails);

    const formInputs = checkoutForm.querySelectorAll('input, select, textarea');
    formInputs.forEach(input => {
        input.addEventListener('input', validateForm);
        input.addEventListener('change', validateForm);
    });

    addressRadios.forEach(radio => {
        radio.addEventListener('change', validateForm);
    });

    if (checkoutForm) {
        checkoutForm.addEventListener('submit', (event) => {
            if (!validateForm()) {
                event.preventDefault();
                const firstInvalidField = checkoutForm.querySelector('.is-invalid:invalid');
                if (firstInvalidField) {
                    firstInvalidField.reportValidity();
                } else {
                    alert('Per favore, seleziona un indirizzo di consegna e compila correttamente tutti i campi richiesti.');
                }
            }
        });
    }

    toggleCardDetails();
    validateForm();
});