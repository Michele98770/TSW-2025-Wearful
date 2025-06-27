document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('loginForm');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const submitButton = form.querySelector('button[type="submit"]');

    const emailError = document.getElementById('emailError');
    const passwordError = document.getElementById('passwordError');

    const emailLabel = document.getElementById('emailLabel');
    const passwordLabel = document.getElementById('passwordLabel');

    function showError(element, errorDiv, labelElement) {
        element.classList.remove('input-success');
        element.classList.add('input-error');
        if (labelElement) {
            labelElement.classList.remove('label-success');
            labelElement.classList.add('label-error');
        }
        errorDiv.textContent = '';
        errorDiv.style.display = 'none';
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

        if (email === '' || !emailPattern.test(email)) {
            showError(emailInput, emailError, emailLabel);
            return false;
        } else {
            showSuccess(emailInput, emailLabel);
            hideError(emailInput, emailError, emailLabel);
            return true;
        }
    }

    function validatePassword() {
        const password = passwordInput.value;
        const minLength = 8;

        if (password === '' || password.length < minLength) {
            showError(passwordInput, passwordError, passwordLabel);
            return false;
        } else {
            showSuccess(passwordInput, passwordLabel);
            hideError(passwordInput, passwordError, passwordLabel);
            return true;
        }
    }

    function updateSubmitButtonState() {
        const isFormValid =
            validateEmail() &&
            validatePassword();

        submitButton.disabled = !isFormValid;
    }

    submitButton.disabled = true;

    emailInput.addEventListener('input', () => {
        validateEmail();
        updateSubmitButtonState();
    });

    passwordInput.addEventListener('input', () => {
        validatePassword();
        updateSubmitButtonState();
    });

    validateEmail();
    validatePassword();
    updateSubmitButtonState();

    form.addEventListener('submit', function(event) {
        if (!(
            validateEmail() &&
            validatePassword()
        )) {
            event.preventDefault();
        }
    });
});