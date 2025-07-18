function updateOrderStatus(selectElement, orderId) {
    const newStatus = selectElement.value;
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '<%= request.getContextPath() %>/UpdateOrderServlet';

    const orderIdInput = document.createElement('input');
    orderIdInput.type = 'hidden';
    orderIdInput.name = 'idOrdine';
    orderIdInput.value = orderId;
    form.appendChild(orderIdInput);

    const statusInput = document.createElement('input');
    statusInput.type = 'hidden';
    statusInput.name = 'nuovoStato';
    statusInput.value = newStatus;
    form.appendChild(statusInput);

    document.body.appendChild(form);
    form.submit();
}

document.querySelectorAll('.orders-table th[data-sort-by]').forEach(header => {
    header.addEventListener('click', function() {
        const sortBy = this.getAttribute('data-sort-by');
        let currentSortBy = '<%= currentSortBy %>';
        let currentSortOrder = '<%= currentSortOrder %>';
        let newSortOrder = 'asc';

        if (sortBy === currentSortBy) {
            newSortOrder = (currentSortOrder === 'asc') ? 'desc' : 'asc';
        }

        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '<%= request.getContextPath() %>/OrdiniAdmin';

        const sortByInput = document.createElement('input');
        sortByInput.type = 'hidden';
        sortByInput.name = 'sortBy';
        sortByInput.value = sortBy;
        form.appendChild(sortByInput);

        const sortOrderInput = document.createElement('input');
        sortOrderInput.type = 'hidden';
        sortOrderInput.name = 'sortOrder';
        sortOrderInput.value = newSortOrder;
        form.appendChild(sortOrderInput);

        const startDateInput = document.createElement('input');
        startDateInput.type = 'hidden';
        startDateInput.name = 'startDate';
        startDateInput.value = startDate;
        form.appendChild(startDateInput);

        const endDateInput = document.createElement('input');
        endDateInput.type = 'hidden';
        endDateInput.name = 'endDate';
        endDateInput.value = endDate;
        form.appendChild(endDateInput);

        document.body.appendChild(form);
        form.submit();
    });
});

function clearDateFilter() {
    document.getElementById('startDate').value = '';
    document.getElementById('endDate').value = '';
    document.getElementById('filterForm').submit();
}