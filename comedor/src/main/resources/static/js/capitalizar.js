 document.addEventListener("DOMContentLoaded", function() {
    const nombreInput = document.getElementById('nombre');
    const apellidoInput = document.getElementById('apellido');

    function capitalizeInput(event) {
    const input = event.target;
    // Convertir todo a minúsculas primero, luego capitalizar la primera letra de cada palabra
    input.value = input.value.toLowerCase().replace(/\b\w/g, function(char) {
    return char.toUpperCase();
});
}

    nombreInput.addEventListener('input', capitalizeInput);
    apellidoInput.addEventListener('input', capitalizeInput);
});
