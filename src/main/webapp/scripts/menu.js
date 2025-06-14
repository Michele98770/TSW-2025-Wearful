document.addEventListener('DOMContentLoaded', function() {
    const hamburgerIcon = document.getElementById('hamburger-icon');
    const mobileMenu = document.getElementById('mobile-menu');
    const closeButton = document.querySelector('.mobile-menu-overlay .close-button');

    function toggleMobileMenu() {
        mobileMenu.classList.toggle('open');
        hamburgerIcon.classList.toggle('open');
    }

    if (hamburgerIcon) {
        hamburgerIcon.addEventListener('click', toggleMobileMenu);
    }

    if (closeButton) {
        closeButton.addEventListener('click', toggleMobileMenu);
    }

    if (mobileMenu) {
        mobileMenu.querySelectorAll('a').forEach(link => {
            link.addEventListener('click', () => {
                if (mobileMenu.classList.contains('open')) {
                    toggleMobileMenu();
                }
            });
        });
    }
});