document.addEventListener('DOMContentLoaded', function() {
    const searchBox = document.querySelector('.box');
    const searchInput = document.getElementById('search');
    const searchIcon = searchBox.querySelector('.material-icons');

    function expandSearch() {
        searchInput.style.width = '250px';
        searchInput.focus();
    }

    function collapseSearchIfEmpty() {
        if (searchInput.value.trim() === '') {
            searchInput.style.width = '0px';
        }
    }

    function performSearch(query) {
        if (query) {
            const targetUrl = `./CatalogoServlet?searchQuery=${encodeURIComponent(query)}`;
            window.location.href = targetUrl;
        }
    }

    searchIcon.addEventListener('mouseover', expandSearch);
    searchIcon.addEventListener('click', expandSearch);

    searchIcon.addEventListener('click', () => {
        const query = searchInput.value.trim();
        performSearch(query);
    });

    searchInput.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            event.preventDefault();
            const query = this.value.trim();
            performSearch(query);
        }
    });

    searchInput.addEventListener('blur', function() {
        setTimeout(() => {
            collapseSearchIfEmpty();
        }, 150);
    });

    if (searchInput.value.trim() !== '') {
        searchInput.style.width = '250px';
        searchInput.focus();
        const valueLength = searchInput.value.length;
        searchInput.setSelectionRange(valueLength, valueLength);
    }
});