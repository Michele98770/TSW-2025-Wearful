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

    function validateEmail() {
        const email = emailInput.value.trim();
        const emailPattern = /^[\w!#$%&'*+/=?`{|}~^-]+(?:\.[\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,6}$/;

        if (email === '') {
            showError(emailInput, '', emailError, emailLabel);
            return false;
        } else if (!emailPattern.test(email)) {
            showError(emailInput, '', emailError, emailLabel);
            return false;
        } else {
            showSuccess(emailInput, emailLabel);
            hideError(emailInput, emailError, emailLabel);
            return true;
        }
    }

    function validateUsername() {
        const username = usernameInput.value.trim();
        if (username === '') {
            showError(usernameInput, '', usernameError, usernameLabel);
            return false;
        } else if (username.length < 3) {
            showError(usernameInput, '', usernameError, usernameLabel);
            return false;
        } else if (username.length > 50) {
            showError(usernameInput, '', usernameError, usernameLabel);
            return false;
        } else {
            showSuccess(usernameInput, usernameLabel);
            hideError(usernameInput, usernameError, usernameLabel);
            return true;
        }
    }

    function validateTelefono() {
        const telefono = telefonoInput.value.trim();
        const telefonoPattern = /^(\+\d{1,4})?\d{10}$/;

        if (telefono === '') {
            showError(telefonoInput, '', telefonoError, telefonoLabel);
            return false;
        } else if (!telefonoPattern.test(telefono)) {
            showError(telefonoInput, '', telefonoError, telefonoLabel);
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

        if (password === '') {
            showError(passwordInput, '', passwordError, passwordLabel);
            return false;
        } else if (password.length < minLength) {
            showError(passwordInput, `La password deve essere di almeno ${minLength} caratteri.`, passwordError, passwordLabel);
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
            showError(confirmPasswordInput, '', confirmPasswordError, confirmPasswordLabel);
            return false;
        } else if (password !== confirmPassword) {
            showError(confirmPasswordInput, 'Le password devono coincidere', confirmPasswordError, confirmPasswordLabel);
            return false;
        } else {
            showSuccess(confirmPasswordInput, confirmPasswordLabel);
            hideError(confirmPasswordInput, confirmPasswordError, confirmPasswordLabel);
            return true;
        }
    }

    function updateSubmitButtonState() {
        const isFormValid =
            validateEmail() &&
            validateUsername() &&
            validateTelefono() &&
            validatePassword() &&
            validateConfirmPassword();

        submitButton.disabled = !isFormValid;
    }

    // Disabilita inizialmente
    submitButton.disabled = true;

    // Event listeners
    emailInput.addEventListener('input', () => {
        validateEmail();
        updateSubmitButtonState();
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
        if (!(
            validateEmail() &&
            validateUsername() &&
            validateTelefono() &&
            validatePassword() &&
            validateConfirmPassword()
        )) {
            event.preventDefault();
        }
    });
});
