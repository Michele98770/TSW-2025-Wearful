document.addEventListener('DOMContentLoaded', function() {
    const filterToggleBtn = document.getElementById('filter-toggle-btn');
    const sidebar = document.getElementById('filter-sidebar');
    const overlay = document.getElementById('page-overlay');
    const closeBtn = document.getElementById('close-sidebar-btn');

    const openSidebar = () => {
        sidebar.classList.add('active');
        overlay.classList.add('active');
        document.body.style.overflow = 'hidden';
    };

    const closeSidebar = () => {
        sidebar.classList.remove('active');
        overlay.classList.remove('active');
        document.body.style.overflow = '';
    };

    if (filterToggleBtn) {
        filterToggleBtn.addEventListener('click', openSidebar);
    }

    if (closeBtn) {
        closeBtn.addEventListener('click', closeSidebar);
    }

    if (overlay) {
        overlay.addEventListener('click', closeSidebar);
    }
});