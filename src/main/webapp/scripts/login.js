document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('loginForm');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const submitButton = form.querySelector('button[type="submit"]');

    const emailError = document.getElementById('emailError');
    const passwordError = document.getElementById('passwordError');

    const emailLabel = document.getElementById('emailLabel');
    const passwordLabel = document.getElementById('passwordLabel');


    function validateEmail() {
        const email = emailInput.value.trim();
        const emailPattern = /^[\w!#$%&'*+/=?`{|}~^-]+(?:\.[\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,6}$/;

        if (email === '' || !emailPattern.test(email)) {
            return false;
        } else {
            return true;
        }
    }

    function validatePassword() {
        const password = passwordInput.value;
        const minLength = 8;

        if (password === '' || password.length < minLength) {
            return false;
        } else {
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