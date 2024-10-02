document.querySelectorAll('.btn-eliminar').forEach(function(button) {
    button.addEventListener('click', function(event) {
        // Mostrar el cuadro de confirmación
        const confirmed = confirm("¿Estás seguro de que deseas eliminar este registro?");

        // Si no está confirmado, redirige a # (no hace nada)
        if (!confirmed) {
            event.preventDefault();
        }
    });
});