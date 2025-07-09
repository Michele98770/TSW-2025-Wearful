document.addEventListener('DOMContentLoaded', () => {
    const menuToggle = document.querySelector('.menu-toggle');
    const mobileMenu = document.getElementById('mobile-menu-panel');
    const overlay = document.querySelector('.overlay');
    const body = document.body;

    if (menuToggle && mobileMenu && overlay) {
        const openMenu = () => {
            mobileMenu.classList.add('active');
            overlay.classList.add('active');
            body.style.overflow = 'hidden';
        };

        const closeMenu = () => {
            mobileMenu.classList.remove('active');
            overlay.classList.remove('active');
            body.style.overflow = '';
        };

        menuToggle.addEventListener('click', (e) => {
            e.stopPropagation();
            if (mobileMenu.classList.contains('active')) {
                closeMenu();
            } else {
                openMenu();
            }
        });

        overlay.addEventListener('click', closeMenu);

        mobileMenu.querySelectorAll('a').forEach(link => {
            link.addEventListener('click', closeMenu);
        });
    }
});