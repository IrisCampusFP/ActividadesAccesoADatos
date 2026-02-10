const tbodyRutas = document.getElementById("tBodyRutas");
const recuadroAlert = document.getElementById("recuadroAlert");

const dialogCrearRuta = document.getElementById("dialogCrearRuta");
const dialogEditarRuta = document.getElementById("dialogEditarRuta");

// Mostrar nombre del usuario (admin) que esté usando el panel en el title
fetch("/user/datos")
    .then(r => r.json())
    .then(data => {
        document.title = "Gestión de rutas (" + data.nombre + ")";
    });

cargarRutas();

// CARGAR LOS DATOS DE LAS RUTAS EN EL BODY DE LA TABLA
function cargarRutas() {
    // Texto que aparece mientras cargan
    tbodyRutas.innerHTML =
        '<tr><td colspan="100%" class="text-center">Cargando rutas...</td></tr>';

    // Fetch que obtiene los datos de los rutas y los muestra
    fetch("/admin/rutas")
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se ha podido cargar la lista de rutas.`; });
            return r.json();
        })
        .then(rutas => {
            if (rutas.length === 0) {
                tbodyRutas.innerHTML =
                    '<tr><td colspan="100%" class="text-center">No hay rutas registradas</td></tr>';
                return;
            }

            // Se muestran los datos de cada ruta fila por fila (tr) en el tBody
            let tBody = "";
            rutas.forEach(r => {
                tBody += `
                    <tr>
                        <td>${r.id}</td>
                        <td>${r.nombre}</td>
                        <td>${r.zona}</td>
                        <td>${r.dia_semana}</td>
                        <td>${formatearHora(r.hora_inicio)} - ${formatearHora(r.hora_fin)}</td>
                        <td>${r.activa ? '<span class="badge text-bg-success">Activa</span>' : '<span class="badge text-bg-danger">Inactiva</span>'}</td>
                        <td>${r.asignaciones !== undefined ? r.asignaciones : 0}</td>
                        <td> 
                            <div class="d-flex gap-2 col-9"> 
                                <button class="btn btn-sm btn-outline-primary col-4" 
                                    onclick="cargarDialogEditar(${r.id})">Editar</button> 
                                <button class="btn btn-sm btn-outline-warning col-4" 
                                    onclick="cambiarEstado(${r.id})">${r.activa ? "Desactivar" : "Activar"} </button> 
                                <button class="btn btn-sm btn-outline-danger col-4" 
                                    onclick="eliminarRuta(${r.id})">Eliminar</button> 
                            </div> 
                        </td>
                    </tr>
                `;
            });
            tbodyRutas.innerHTML = tBody;
        })
        .catch((error) =>
            mostrarError(error)
        );
}

// Función para formatear hora (mostrar solo HH:MM sin segundos)
function formatearHora(hora) {
    if (!hora) return "-";
    // Si viene en formato HH:MM:SS, toma solo HH:MM
    return hora.substring(0, 5);
}

function mostrarError(msg) {
    recuadroAlert.textContent = msg;
    recuadroAlert.classList.remove("d-none");

    setTimeout(() => {
        recuadroAlert.textContent = "";
        recuadroAlert.classList.add("d-none");
    }, 3000);
}

// CREAR NUEVO RUTA
// Al pulsar el botón 'Crear nuevo ruta' se abre el modal (dialog) con el formulario
document.getElementById("btnCrearRuta").onclick = () => {
    dialogCrearRuta.showModal();
};

const msgCrearError = document.getElementById("msgCrearError");

// Al enviar el formulario se llama a la función crear ruta
document.getElementById("formCrearRuta").onsubmit = e => {
    e.preventDefault();
    crearRuta();
};

// Función que envía al backend los datos del nuevo ruta, se guardan y se recarga la tabla
function crearRuta() {
    fetch("/admin/rutas", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            nombre: document.getElementById("nombreCrear").value.trim(),
            zona: document.getElementById("zonaCrear").value.trim(),
            dia_semana: document.getElementById("diaSemanaCrear").value,
            hora_inicio: document.getElementById("horaInicioCrear").value,
            hora_fin: document.getElementById("horaFinCrear").value,
            activa: document.getElementById("activaCrear").value
        })
    })
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se ha podido crear la ruta.`; });
            return r.json();
        })
        .then(() => {
            dialogCrearRuta.close();
            cargarRutas();
        })
        .catch(e => {
            msgCrearError.textContent = e;
            msgCrearError.classList.remove("d-none");
        });
}

dialogCrearRuta.addEventListener("close", () => {
    document.getElementById("formCrearRuta").reset();
    msgCrearError.classList.add("d-none");
});


// EDITAR RUTA

// Al pulsar el botón editar se llama a esta función que obtiene los datos del ruta por su id
function cargarDialogEditar(id) {
    fetch("/admin/rutas/" + id)
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se han podido cargar los datos de la ruta.`; });
            return r.json();
        })
        .then(r => {
            document.getElementById("idRutaEditado").value = r.id;
            document.getElementById("nombreEditar").value = r.nombre;
            document.getElementById("zonaEditar").value = r.zona;
            document.getElementById("diaSemanaEditar").value = r.dia_semana;
            document.getElementById("horaInicioEditar").value = r.hora_inicio;
            document.getElementById("horaFinEditar").value = r.hora_fin;
            document.getElementById("activaEditar").value = r.activa;

            dialogEditarRuta.showModal();
        })
        .catch((error) =>
            alert(error)
        );
}

// Al enviar el formulario se llama a la función editar ruta
document.getElementById("formEditarRuta").onsubmit = e => {
    e.preventDefault();
    editarRuta();
};

function editarRuta() {
    const id = document.getElementById("idRutaEditado").value;

    fetch("/admin/rutas/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            nombre: document.getElementById("nombreEditar").value.trim(),
            zona: document.getElementById("zonaEditar").value.trim(),
            dia_semana: document.getElementById("diaSemanaEditar").value,
            hora_inicio: document.getElementById("horaInicioEditar").value,
            hora_fin: document.getElementById("horaFinEditar").value,
            activa: document.getElementById("activaEditar").value
        })
    })
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status} No se ha podido editar la ruta.`; });
            dialogEditarRuta.close();
            cargarRutas();
        })
        .catch((error) =>
            alert(error)
        );
}

// MODIFICAR ESTADO RUTA (Interruptor)
function cambiarEstado(id) {
    fetch("/admin/rutas/" + id + "/estado", { method: "PUT" })
        .then(cargarRutas)
        .catch(() => mostrarError("Error al modificar estado"));
}

// ELIMINAR RUTA
function eliminarRuta(id) {
    // Se pide confirmación
    if (!confirm(`¿Estás seguro/a de que quieres eliminar la ruta con id ${id}?`)) return;

    // Se hace un fetch con metodo DELETE para eliminar el ruta de la base de datos
    fetch("/admin/rutas/" + id, { method: "DELETE" })
        .then(cargarRutas) // Se recarga la tabla
        .catch(() => mostrarError("Error al eliminar ruta."));
}
