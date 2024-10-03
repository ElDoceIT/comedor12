document.getElementById("usuarioNom").addEventListener("input", function() {
    const input = this.value;

    if (input.length > 2) { // Comienza a buscar cuando el input tiene más de 2 caracteres
        fetch(`/consumos/usuarios/buscar?query=${input}`)
            .then(response => response.json())
            .then(data => {
                const sugerenciasDiv = document.getElementById("sugerencias-usuario");
                sugerenciasDiv.innerHTML = ""; // Limpiar resultados anteriores

                data.forEach(usuario => {
                    const suggestion = document.createElement("div");
                    suggestion.classList.add("list-group-item");
                    suggestion.textContent = `${usuario.apellido} ${usuario.nombre}`; // Mostrar apellido y nombre
                    suggestion.onclick = () => seleccionarUsuario(usuario.id_usuario, usuario.apellido + ' ' + usuario.nombre); // Guardar id_usuario
                    sugerenciasDiv.appendChild(suggestion);
                });
            });
    }
});

function seleccionarUsuario(idUsuario, nombreCompleto) {
    document.getElementById("usuarioNom").value = nombreCompleto; // Asignar el nombre completo al campo visible
    document.getElementById("usuarioId").value = idUsuario; // Asignar el id_usuario al campo oculto
    document.getElementById("sugerencias-usuario").innerHTML = ""; // Limpiar las sugerencias
}

