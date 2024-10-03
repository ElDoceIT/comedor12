document.getElementById("producto").addEventListener("input", function() {
    const input = this.value;

    if (input.length > 3) { // Comienza a buscar cuando el input tiene más de 3 caracteres
        fetch(`/consumos/productos/buscar?query=${input}`)
            .then(response => response.json())
            .then(data => {
                const sugerenciasDiv = document.getElementById("sugerencias-producto");
                sugerenciasDiv.innerHTML = ""; // Limpiar resultados anteriores

                data.forEach(producto => {
                    const suggestion = document.createElement("div");
                    suggestion.classList.add("list-group-item");
                    suggestion.textContent = producto.descripcion;
                    suggestion.onclick = () => seleccionarProducto(producto.id_producto, producto.descripcion); // Asignar id_producto y descripcion
                    sugerenciasDiv.appendChild(suggestion);
                });
            });
    }
});

function seleccionarProducto(idProducto, descripcion) {
    document.getElementById("producto").value = descripcion; // Asignar la descripción al input visible
    document.getElementById("productoId").value = idProducto; // Asignar el id_producto al campo oculto
    document.getElementById("sugerencias-producto").innerHTML = ""; // Limpiar las sugerencias
}
