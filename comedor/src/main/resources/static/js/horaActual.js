function actualizarHora() {
    const spanHora = document.getElementById("hora-actual");

    // Obtener la hora actual del sistema del cliente
    const ahora = new Date();

    // Formatear la hora (puedes personalizar el formato según tus necesidades)
    const opciones = {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    };
    const horaFormateada = ahora.toLocaleDateString('es-ES', opciones);

    // Actualizar el contenido del span con la hora formateada
    spanHora.textContent = horaFormateada;
}

// Llamar a la función cada segundo (1000ms)
setInterval(actualizarHora, 1000);

// Llamar a la función al cargar la página para mostrar la hora inmediatamente
actualizarHora();