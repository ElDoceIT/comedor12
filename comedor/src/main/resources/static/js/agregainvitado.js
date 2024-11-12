// Contador para asignar identificadores únicos a cada invitado
let contadorInvitados = 0;

// Función para agregar campos de entrada para un nuevo invitado
function agregarInvitado() {
    contadorInvitados++;
    const container = document.getElementById('invitados-container');
    const invitadoDiv = document.createElement('div');
    invitadoDiv.classList.add('mb-2', 'invitado');
    invitadoDiv.id = 'invitado_' + contadorInvitados;
    invitadoDiv.innerHTML = `
            <div class="form-group">
                <input type="text" class="form-control mb-1" name="invitados[${contadorInvitados}].nombre" placeholder="Nombre del invitado" required>
                <button type="button" class="btn btn-sm btn-outline-danger mt-1" onclick="eliminarInvitado(${contadorInvitados})">
                    <i class="bi bi-trash"></i> Quitar
                </button>
            </div>`;
    container.appendChild(invitadoDiv);
}

// Función para eliminar un invitado
function eliminarInvitado(id) {
    const invitado = document.getElementById('invitado_' + id);
    if (invitado) {
        invitado.remove();
    }
}