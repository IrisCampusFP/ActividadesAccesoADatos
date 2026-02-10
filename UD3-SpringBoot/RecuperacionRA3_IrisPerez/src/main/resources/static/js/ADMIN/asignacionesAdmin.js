const tbodyAsignaciones = document.getElementById("tBodyAsignaciones");
const recuadroAlert = document.getElementById("recuadroAlert");

const dialogCrearAsignacion = document.getElementById("dialogCrearAsignacion");

// Mostrar nombre del usuario (admin) que esté usando el panel en el title
fetch("/user/datos")
    .then(r => r.json())
    .then(data => {
        document.title = "Gestión de asignaciones (" + data.nombre + ")";
    });

cargarAsignaciones();

// CARGAR LOS DATOS DE LAS ASIGNACIONES EN EL BODY DE LA TABLA
function cargarAsignaciones() {
    // Texto que aparece mientras cargan
    tbodyAsignaciones.innerHTML =
        '<tr><td colspan="100%" class="text-center">Cargando asignaciones...</td></tr>';

    // Fetch que obtiene los datos de las asignaciones y los muestra
    fetch("/admin/asignaciones")
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se ha podido cargar la lista de asignaciones.`; });
            return r.json();
        })
        .then(asignaciones => {
            if (asignaciones.length === 0) {
                tbodyAsignaciones.innerHTML =
                    '<tr><td colspan="100%" class="text-center">No hay asignaciones registradas</td></tr>';
                return;
            }

            // Se muestran los datos de cada asignacion fila por fila (tr) en el tBody
            let tBody = "";
            asignaciones.forEach(a => {
                tBody += `
                    <tr>
                        <td>${a.id}</td>
                        <td>${a.matricula}</td>
                        <td>${a.modelo}</td>
                        <td>${a.nombreRuta}</td>
                        <td>${a.zona || "-"}</td>
                        <td>${a.diaSemana || "-"}</td>
                        <td>${a.fechaAsignacion ? new Date(a.fechaAsignacion).toLocaleDateString() : "-"}</td>
                        <td>
                            <div class="d-flex gap-2 justify-content-center">
                                <button class="btn btn-sm btn-outline-danger" onclick="eliminarAsignacion(${a.id})">Eliminar</button>
                            </div>
                        </td>
                    </tr>
                `;
            });
            tbodyAsignaciones.innerHTML = tBody;
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


// CREAR NUEVA ASIGNACION

// Al pulsar el botón 'Crear nueva asignación' se abre el modal (dialog) con el formulario
document.getElementById("btnCrearAsignacion").onclick = () => {
    cargarDialogCrear();
};

// Función que carga los camiones y rutas disponibles en los selects del dialog
function cargarDialogCrear() {
    document.getElementById("msgCrearError").classList.add("d-none");

    // Cargar camiones disponibles
    fetch("/admin/camiones")
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se han podido cargar los camiones.`; });
            return r.json();
        })
        .then(camiones => {
            const selectCamion = document.getElementById("camionCrear");
            selectCamion.innerHTML = '<option value="">Seleccionar camión...</option>';
            camiones.forEach(c => {
                selectCamion.innerHTML += `<option value="${c.id}">${c.matricula} - ${c.modelo}</option>`;
            });

            // Cargar rutas disponibles
            return fetch("/admin/rutas");
        })
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se han podido cargar las rutas.`; });
            return r.json();
        })
        .then(rutas => {
            const selectRuta = document.getElementById("rutaCrear");
            selectRuta.innerHTML = '<option value="">Seleccionar ruta...</option>';
            rutas.forEach(r => {
                selectRuta.innerHTML += `<option value="${r.id}">${r.nombre} - ${r.zona} (${r.dia_semana})</option>`;
            });

            dialogCrearAsignacion.showModal(); // Se muestra el dialog
        })
        .catch((error) => {
            alert(error);
        });
}

// Al enviar el formulario se llama a la función crear asignacion
document.getElementById("formCrearAsignacion").onsubmit = (e) => {
    e.preventDefault();
    crearAsignacion();
};

const msgCrearError = document.getElementById("msgCrearError");

// Función que envía los datos del formulario al backend
function crearAsignacion() {
    fetch("/admin/asignaciones", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            camionId: parseInt(document.getElementById("camionCrear").value),
            rutaId: parseInt(document.getElementById("rutaCrear").value)
        })
    }).then((r) => {
        if (!r.ok) return r.json()
            .then(data => {
                throw data.errorMsg || `Error: ${r.status}. No se ha podido crear la asignación.`
            });
        dialogCrearAsignacion.close(); // Se cierra el dialog
        cargarAsignaciones(); // Se recargan los datos mostrados en la tabla
    }).catch((error) => {
        msgCrearError.textContent = error;
        msgCrearError.classList.remove("d-none");
    });
}


// ELIMINAR ASIGNACION

// Función que elimina una asignacion
function eliminarAsignacion(id) {
    // Se pide confirmación
    if (!confirm("¿Estás seguro de que quieres eliminar esta asignación?")) return;

    fetch("/admin/asignaciones/" + id, {
        method: "DELETE"
    }).then((r) => {
        if (!r.ok) return r.json()
            .then(data => {
                throw data.errorMsg || `Error: ${r.status}. No se ha podido eliminar la asignación.`
            });
        cargarAsignaciones(); // Se recargan los datos mostrados en la tabla
    }).catch((error) => {
        mostrarError(error);
    });
}
