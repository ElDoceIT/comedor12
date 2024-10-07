document.getElementById('comida').addEventListener('keyup', function () {
    const query = this.value;

    if (query.length >= 3) { // Solo buscar si hay más de 3 caracteres
        fetch(`/menu/buscarComidas?query=` + query)
            .then(response => response.json())
            .then(data => {
                const sugerenciasDiv = document.getElementById('comida-sugerencias');
                sugerenciasDiv.innerHTML = '';  // Limpiar resultados anteriores

                data.forEach(comida => {
                    const opcion = document.createElement('a');
                    opcion.href = "#";
                    opcion.classList.add('list-group-item', 'list-group-item-action');
                    let tipoComidaTexto = '';
                    switch (comida.tipo_comida) {
                        case 1:
                            tipoComidaTexto = 'Principal';
                            break;
                        case 2:
                            tipoComidaTexto = 'Light';
                            break;
                        case 3:
                            tipoComidaTexto = 'Celiaco';
                            break;
                        case 4:
                            tipoComidaTexto = 'Fruta';
                            break;
                        default:
                            tipoComidaTexto = 'Otro';
                    }
                    // Mostrar entrada, principal, postre y tipo_comida en las sugerencias
                    opcion.textContent = comida.entrada + ' --- ' + comida.principal + ' --- ' + comida.postre + ' --- ' +  tipoComidaTexto;



                    // Al seleccionar una comida, almacenar el comidaID en el campo oculto
                    opcion.addEventListener('click', function () {
                        document.getElementById('comida').value = comida.entrada + ' --- ' + comida.principal + ' --- ' + comida.postre + ' --- ' + tipoComidaTexto;
                        document.getElementById('comidaId').value = comida.id_comida;  // Guardar comidaID en campo oculto
                        sugerenciasDiv.innerHTML = '';  // Limpiar sugerencias después de seleccionar
                    });

                    sugerenciasDiv.appendChild(opcion);
                });
            });
    }
});