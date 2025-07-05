document.addEventListener('DOMContentLoaded', function() {
    const searchBox = document.querySelector('.box');
    const searchInput = document.getElementById('search');
    const searchIcon = searchBox.querySelector('.material-icons');

    let searchTimeout = null;

    function expandSearch() {
        searchInput.style.width = '250px';
        searchInput.focus();
    }

    function collapseSearchIfEmpty() {
        if (searchInput.value.trim() === '') {
            searchInput.style.width = '0px';
        }
    }

    searchIcon.addEventListener('mouseover', function (){
        expandSearch();
    });

    searchInput.addEventListener('blur', function() {
        setTimeout(() => {
            collapseSearchIfEmpty();
        }, 100);
    });

    searchInput.addEventListener('input', function() {
        clearTimeout(searchTimeout);
        const query = this.value.trim();

        if (query.length === 0) {
            collapseSearchIfEmpty();
            return;
        }

        expandSearch();

        searchTimeout = setTimeout(() => {
            const targetUrl = `./CatalogoServlet?searchQuery=${encodeURIComponent(query)}`;
            window.location.href = targetUrl;
        }, 500);
    });

    searchInput.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            event.preventDefault();
            clearTimeout(searchTimeout);
            const query = this.value.trim();
            const targetUrl = `./CatalogoServlet?searchQuery=${encodeURIComponent(query)}`;
            window.location.href = targetUrl;
        }
    });

    searchIcon.addEventListener('click', function() {
        expandSearch();
    });

    if (searchInput.value.trim() !== '') {
        searchInput.style.width = '250px';

        searchInput.focus();
        const valueLength = searchInput.value.length;
        searchInput.setSelectionRange(valueLength, valueLength);
    }
});