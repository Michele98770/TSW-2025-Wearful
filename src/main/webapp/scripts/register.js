document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('registrationForm');
    const emailInput = document.getElementById('email');
    const usernameInput = document.getElementById('username');
    const telefonoInput = document.getElementById('telefono');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirm_password');
    const submitButton = form.querySelector('button[type="submit"]');

    const emailError = document.getElementById('emailError');
    const usernameError = document.getElementById('usernameError');
    const telefonoError = document.getElementById('telefonoError');
    const passwordError = document.getElementById('passwordError');
    const confirmPasswordError = document.getElementById('confirmPasswordError');

    const emailLabel = document.getElementById('emailLabel');
    const usernameLabel = document.getElementById('usernameLabel');
    const telefonoLabel = document.getElementById('telefonoLabel');
    const passwordLabel = document.getElementById('passwordLabel');
    const confirmPasswordLabel = document.getElementById('confirmPasswordLabel');

    let emailCheckTimeout = null;

    function showError(element, message, errorDiv, labelElement) {
        element.classList.remove('input-success');
        element.classList.add('input-error');
        if (labelElement) {
            labelElement.classList.remove('label-success');
            labelElement.classList.add('label-error');
        }
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
    }

    function showSuccess(element, labelElement) {
        element.classList.remove('input-error');
        element.classList.add('input-success');
        if (labelElement) {
            labelElement.classList.remove('label-error');
            labelElement.classList.add('label-success');
        }
    }

    function hideError(element, errorDiv, labelElement) {
        element.classList.remove('input-error');
        if (labelElement) {
            labelElement.classList.remove('label-error');
            labelElement.classList.remove('label-success');
        }
        errorDiv.textContent = '';
        errorDiv.style.display = 'none';
    }

    function validateEmail(callback) {
        const email = emailInput.value.trim();
        const emailPattern = /^[\w!#$%&'*+/=?`{|}~^-]+(?:\.[\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,6}$/;

        if (email === '') {
            showError(emailInput, 'L\'email non può essere vuota.', emailError, emailLabel);
            if (callback) callback(false);
            return false;
        } else if (!emailPattern.test(email)) {
            showError(emailInput, 'Formato email non valido.', emailError, emailLabel);
            if (callback) callback(false);
            return false;
        } else {
            showSuccess(emailInput, emailLabel);
            hideError(emailInput, emailError, emailLabel);

            clearTimeout(emailCheckTimeout);
            emailCheckTimeout = setTimeout(() => {
                fetch(`./CheckEmailServlet?email=${encodeURIComponent(email)}`)
                    .then(response => response.json())
                    .then(data => {
                        if (data.error) {
                            showError(emailInput, data.error, emailError, emailLabel);
                            if (callback) callback(false);
                        } else if (!data.available) {
                            showError(emailInput, 'Questa email è già registrata.', emailError, emailLabel);
                            if (callback) callback(false);
                        } else {
                            showSuccess(emailInput, emailLabel);
                            hideError(emailInput, emailError, emailLabel);
                            if (callback) callback(true);
                        }
                        updateSubmitButtonState();
                    })
                    .catch(error => {
                        console.error('Errore durante la verifica email:', error);
                        showError(emailInput, 'Errore di connessione o del server. Riprova.', emailError, emailLabel);
                        if (callback) callback(false);
                        updateSubmitButtonState();
                    });
            }, 500);

            return true;
        }
    }

    function validateUsername() {
        const username = usernameInput.value.trim();
        if (username === '') {
            showError(usernameInput, 'Il nome utente non può essere vuoto.', usernameError, usernameLabel);
            return false;
        } else if (username.length < 3) {
            showError(usernameInput, 'Il nome utente deve essere di almeno 3 caratteri.', usernameError, usernameLabel);
            return false;
        } else if (username.length > 50) {
            showError(usernameInput, 'Il nome utente non può superare i 50 caratteri.', usernameError, usernameLabel);
            return false;
        } else {
            showSuccess(usernameInput, usernameLabel);
            hideError(usernameInput, usernameError, usernameLabel);
            return true;
        }
    }

    function validateTelefono() {
        const telefono = telefonoInput.value.trim();
        const telefonoPattern = /^(?:(?:\+|00)39)?(?:\d{2,3}(?: |\-)?\d{6,7}|\d{3}(?: |\-)?\d{7}|3\d{2}(?: |\-)?\d{6,7})$/;


        if (telefono === '') {
            showError(telefonoInput, 'Il numero di telefono non può essere vuoto.', telefonoError, telefonoLabel);
            return false;
        } else if (!telefonoPattern.test(telefono)) {
            showError(telefonoInput, 'Formato telefono non valido.', telefonoError, telefonoLabel);
            return false;
        } else {
            showSuccess(telefonoInput, telefonoLabel);
            hideError(telefonoInput, telefonoError, telefonoLabel);
            return true;
        }
    }

    function validatePassword() {
        const password = passwordInput.value;
        const minLength = 8;

        const passwordStrongPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/;


        if (password === '') {
            showError(passwordInput, 'La password non può essere vuota.', passwordError, passwordLabel);
            return false;
        } else if (password.length < minLength) {
            showError(passwordInput, `La password deve essere di almeno ${minLength} caratteri.`, passwordError, passwordLabel);
            return false;
        } else if (!passwordStrongPattern.test(password)) {
            showError(passwordInput, 'La password deve contenere almeno una maiuscola, una minuscola, un numero e un carattere speciale.', passwordError, passwordLabel);
            return false;
        } else {
            showSuccess(passwordInput, passwordLabel);
            hideError(passwordInput, passwordError, passwordLabel);
            return true;
        }
    }

    function validateConfirmPassword() {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        if (confirmPassword === '') {
            showError(confirmPasswordInput, 'Conferma password non può essere vuota.', confirmPasswordError, confirmPasswordLabel);
            return false;
        } else if (password !== confirmPassword) {
            showError(confirmPasswordInput, 'Le password devono coincidere.', confirmPasswordError, confirmPasswordLabel);
            return false;
        } else {
            showSuccess(confirmPasswordInput, confirmPasswordLabel);
            hideError(confirmPasswordInput, confirmPasswordError, confirmPasswordLabel);
            return true;
        }
    }

    let isEmailAvailable = false;

    function updateSubmitButtonState() {
        const isFormValidExcludingEmail =
            validateUsername() &&
            validateTelefono() &&
            validatePassword() &&
            validateConfirmPassword();

        submitButton.disabled = !(isFormValidExcludingEmail && isEmailAvailable);
    }

    submitButton.disabled = true;

    emailInput.addEventListener('input', () => {
        validateEmail((isValid) => {
            isEmailAvailable = isValid;
            updateSubmitButtonState();
        });
    });

    usernameInput.addEventListener('input', () => {
        validateUsername();
        updateSubmitButtonState();
    });

    telefonoInput.addEventListener('input', () => {
        validateTelefono();
        updateSubmitButtonState();
    });

    passwordInput.addEventListener('input', () => {
        validatePassword();
        validateConfirmPassword();
        updateSubmitButtonState();
    });

    confirmPasswordInput.addEventListener('input', () => {
        validateConfirmPassword();
        updateSubmitButtonState();
    });

    form.addEventListener('submit', function(event) {
        event.preventDefault();

        const isSyncValid = validateUsername() && validateTelefono() && validatePassword() && validateConfirmPassword();

        if (isSyncValid) {
            validateEmail((emailIsValid) => {
                if (emailIsValid) {
                    form.submit();
                } else {
                    console.log('Email non valida o non disponibile. Submit bloccato.');
                    updateSubmitButtonState();
                }
            });
        } else {
            console.log('Validazione sincrona fallita. Submit bloccato.');
            updateSubmitButtonState();
        }
    });
});