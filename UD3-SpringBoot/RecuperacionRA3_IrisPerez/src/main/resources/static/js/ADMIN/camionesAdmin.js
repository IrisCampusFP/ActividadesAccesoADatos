const tbodyCamiones = document.getElementById("tBodyCamiones");
const recuadroAlert = document.getElementById("recuadroAlert");

const dialogCrearCamion = document.getElementById("dialogCrearCamion");
const dialogEditarCamion = document.getElementById("dialogEditarCamion");

// Mostrar nombre del usuario (admin) que esté usando el panel en el title
fetch("/user/datos")
    .then(r => r.json())
    .then(data => {
        document.title = "Camiones Registrados (" + data.nombre + ")";
    });

cargarCamiones();

// CARGAR LOS DATOS DE LOS CAMIONES EN EL BODY DE LA TABLA
function cargarCamiones() {
    // Texto que aparece mientras cargan
    tbodyCamiones.innerHTML =
        '<tr><td colspan="100%" class="text-center">Cargando camiones...</td></tr>';

    // Fetch que obtiene los datos de los camiones y los muestra
    fetch("/admin/camiones")
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se ha podido cargar la lista de camiones.`; });
            return r.json();
        })
        .then(camiones => {
            if (camiones.length === 0) {
                tbodyCamiones.innerHTML =
                    '<tr><td colspan="100%" class="text-center">No hay camiones registrados</td></tr>';
                return;
            }

            // Se muestran los datos de cada camion fila por fila (tr) en el tBody
            let tBody = "";
            camiones.forEach(c => {
                tBody += `
                    <tr>
                        <td>${c.id}</td>
                        <td>${c.matricula}</td>
                        <td>${c.modelo}</td>
                        <td>${c.capacidad_kg}</td>
                        <td>${c.estado}</td>
                        <td>${c.fechaAlta ? new Date(c.fechaAlta).toLocaleDateString() : "-"}</td>
                        <td>${c.activo ? '<span class="badge text-bg-success">Activo</span>' : '<span class="badge text-bg-danger">Inactivo</span>'}</td>
                        <td>${c.asignaciones !== undefined ? c.asignaciones : 0}</td>
                        <td> 
                            <div class="d-flex gap-2 col-9"> 
                                <button class="btn btn-sm btn-outline-primary col-5" 
                                    onclick="cargarDialogEditar(${c.id})">Editar</button> 
                                <button class="btn btn-sm btn-outline-warning col-5" 
                                    onclick="cambiarEstado(${c.id})">${c.activo ? "Desactivar" : "Activar"} </button> 
                                <button class="btn btn-sm btn-outline-danger col-5" 
                                    onclick="eliminarCamion(${c.id})">Eliminar</button> 
                            </div> 
                        </td>
                    </tr>
                `;
            });
            tbodyCamiones.innerHTML = tBody;
        })
        .catch((error) =>
            mostrarError(error)
        );
}

function mostrarError(msg) {
    recuadroAlert.textContent = msg;
    recuadroAlert.classList.remove("d-none");

    setTimeout(() => {
        recuadroAlert.textContent = "";
        recuadroAlert.classList.add("d-none");
    }, 3000);
}

// CREAR NUEVO CAMION
// Al pulsar el botón 'Crear nuevo camion' se abre el modal (dialog) con el formulario
document.getElementById("btnCrearCamion").onclick = () => {
    dialogCrearCamion.showModal();
};

const msgCrearError = document.getElementById("msgCrearError");

// Al enviar el formulario se llama a la función crear camion
document.getElementById("formCrearCamion").onsubmit = e => {
    e.preventDefault();
    crearCamion();
};

// Función que envía al backend los datos del nuevo camion, se guardan y se recarga la tabla
function crearCamion() {
    fetch("/admin/camiones", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            matricula: document.getElementById("matriculaCrear").value.trim(),
            modelo: document.getElementById("modeloCrear").value.trim(),
            capacidad_kg: parseFloat(document.getElementById("capacidadCrear").value),
            estado: document.getElementById("estadoCrear").value,
            fechaAlta: document.getElementById("fechaAltaCrear").value || null,
            activo: document.getElementById("activoCrear").value
        })
    })
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se ha podido crear el camion.`; });
            return r.json();
        })
        .then(() => {
            dialogCrearCamion.close();
            cargarCamiones();
        })
        .catch(e => {
            msgCrearError.textContent = e;
            msgCrearError.classList.remove("d-none");
        });
}

dialogCrearCamion.addEventListener("close", () => {
    document.getElementById("formCrearCamion").reset();
    msgCrearError.classList.add("d-none");
});


// EDITAR CAMION

// Al pulsar el botón editar se llama a esta función que obtiene los datos del camion por su id
function cargarDialogEditar(id) {
    fetch("/admin/camiones/" + id)
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se han podido cargar los datos del camion.`; });
            return r.json();
        })
        .then(c => {
            document.getElementById("idCamionEditado").value = c.id;
            document.getElementById("matriculaEditar").value = c.matricula;
            document.getElementById("modeloEditar").value = c.modelo;
            document.getElementById("capacidadEditar").value = c.capacidad_kg;
            document.getElementById("estadoEditar").value = c.estado;
            document.getElementById("fechaAltaEditar").value = c.fechaAlta;
            document.getElementById("activoEditar").value = c.activo;

            dialogEditarCamion.showModal();
        })
        .catch((error) =>
            alert(error)
        );
}

// Al enviar el formulario se llama a la función editar camion
document.getElementById("formEditarCamion").onsubmit = e => {
    e.preventDefault();
    editarCamion();
};

function editarCamion() {
    const id = document.getElementById("idCamionEditado").value;

    fetch("/admin/camiones/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            matricula: document.getElementById("matriculaEditar").value.trim(),
            modelo: document.getElementById("modeloEditar").value.trim(),
            capacidad_kg: parseFloat(document.getElementById("capacidadEditar").value),
            estado: document.getElementById("estadoEditar").value,
            fechaAlta: document.getElementById("fechaAltaEditar").value || null,
            activo: document.getElementById("activoEditar").value
        })
    })
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status} No se ha podido editar el camion.`; });
            dialogEditarCamion.close();
            cargarCamiones();
        })
        .catch((error) =>
            alert(error)
        );
}

// MODIFICAR ESTADO CAMION (Interruptor)
function cambiarEstado(id) {
    fetch("/admin/camiones/" + id + "/estado", { method: "PUT" })
        .then(cargarCamiones)
        .catch(() => mostrarError("Error al modificar estado"));
}

// ELIMINAR CAMION
function eliminarCamion(id) {
    // Se pide confirmación
    if (!confirm(`¿Estás seguro/a de que quieres eliminar el camion con id ${id}?`)) return;

    // Se hace un fetch con metodo DELETE para eliminar el camion de la base de datos
    fetch("/admin/camiones/" + id, { method: "DELETE" })
        .then(cargarCamiones) // Se recarga la tabla
        .catch(() => mostrarError("Error al eliminar camion."));
}