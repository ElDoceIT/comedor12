alert("¡El script se está ejecutando!");
const listaMenus = document.getElementById('listaMenus');
const fechaInput = document.getElementById('fecha');
const menuInput = document.getElementById('menu');
const listaMenus = document.getElementById('listaMenus');
function agregarItem() {
  const fecha = fechaInput.value;
  const menu = menuInput.value;

  if (fecha && menu) {
    const newItem = document.createElement('li');
    newItem.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center', 'px-3');

    // Convertir la fecha al formato dd/mm/aaaa en UTC
    const fechaFormateada = new Date(fecha).toLocaleDateString('es-ES', { timeZone: 'UTC' });

    const itemContent = document.createElement('div');
    itemContent.textContent = `${fechaFormateada} - ${menu}`;

    newItem.appendChild(itemContent);

    // boton para eliminar
    const btnEliminar = document.createElement('button');
    btnEliminar.classList.add('btn', 'btn-danger', 'btn-sm', 'rounded-circle');
    btnEliminar.textContent = 'X';
    btnEliminar.addEventListener('click', () => {
      newItem.remove();
    });
    newItem.appendChild(btnEliminar);

    listaMenus.appendChild(newItem);
    menuInput.value = '';
  }
}

// Evita el envío del formulario
const formulario = document.querySelector('form');
formulario.addEventListener('submit', (event) => {
  event.preventDefault();
});