const listaMenus = document.getElementById('listaMenus');
const fechaInput = document.getElementById('fecha');
const menuInput = document.getElementById('menu');
const agregarButton = document.getElementById('button-addon2');

// Función para agregar un nuevo ítem a la lista
function agregarItem() {
  const fecha = fechaInput.value;
  const menu = menuInput.value;

  // Verificamos si se ingresaron tanto la fecha como el menú
  if (fecha && menu) {
    const newItem = document.createElement('li');
    newItem.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center', 'px-3');

    // Convertimos la fecha al formato dd/mm/aaaa en UTC
    const fechaFormateada = new Date(fecha).toLocaleDateString('es-ES', { timeZone: 'UTC' });

    const itemContent = document.createElement('div');
    itemContent.textContent = `${fechaFormateada} - ${menu}`;

    newItem.appendChild(itemContent);

    // Botón para eliminar el ítem
    const btnEliminar = document.createElement('button');
    btnEliminar.classList.add('btn', 'btn-danger', 'btn-sm', 'rounded-circle');
    btnEliminar.textContent = 'X';
    btnEliminar.addEventListener('click', () => {
      newItem.remove();
    });
    newItem.appendChild(btnEliminar);

    listaMenus.appendChild(newItem);
    menuInput.value = ''; // Limpiamos el campo de entrada del menú
  } else {
    // Mostrar un mensaje de error si no se ingresaron todos los datos
    alert('Por favor, ingresa tanto la fecha como el menú.');
  }
}

// Agregamos un evento de clic al botón "Agregar"
agregarButton.addEventListener('click', agregarItem);

// Evitamos que el formulario se envíe de forma predeterminada
const formulario = document.querySelector('form');
formulario.addEventListener('submit', (event) => {
  event.preventDefault();
});