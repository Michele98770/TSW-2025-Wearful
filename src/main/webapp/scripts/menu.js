document.addEventListener('DOMContentLoaded', function() {


    const currentPath = window.location.pathname;


    const menuLinks = document.querySelectorAll('.menu a');

    menuLinks.forEach(link => {

        const linkPath = new URL(link.href).pathname;

        if (currentPath === linkPath) {
            link.classList.add('active-link');
        }
    });

    const menuToggleBtn = document.getElementById('menu-toggle-btn');
    const mobileMenu = document.getElementById('mobile-menu-container');
    const overlay = document.getElementById('overlay');

    function closeMenu() {
        if (mobileMenu) mobileMenu.classList.remove('active');
        if (overlay) overlay.classList.remove('active');
    }

    if (menuToggleBtn && mobileMenu && overlay) {
        menuToggleBtn.addEventListener('click', function() {
            mobileMenu.classList.toggle('active');
            overlay.classList.toggle('active');
        });

        overlay.addEventListener('click', closeMenu);
    }
});