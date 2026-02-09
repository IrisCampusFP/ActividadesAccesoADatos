const tbodyAsignaciones = document.getElementById("tBodyAsignaciones");
const recuadroAlert = document.getElementById("recuadroAlert");

const dialogCrearAsignacion = document.getElementById("dialogCrearAsignacion");
const dialogEditarAsignacion = document.getElementById("dialogEditarAsignacion");
const dialogAsignarMedico = document.getElementById("dialogAsignarMedico");

// Mostrar nombre del usuario (admin) que esté usando el panel en el title
fetch("/user/datos")
    .then(r => r.json())
    .then(data => {
        document.title = "Gestión de asignaciones (" + data.nombre + ")";
    });

cargarAsignaciones();

// CARGAR LOS DATOS DE LOS PACIENTES EN EL BODY DE LA TABLA
function cargarAsignaciones() {
    // Texto que aparece mientras cargan
    tbodyAsignaciones.innerHTML =
        '<tr><td colspan="100%" class="text-center">Cargando asignaciones...</td></tr>';

    // Fetch que obtiene los datos de los asignaciones y los muestra
    fetch("/admin/asignaciones")
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se ha podido cargar la lista de asignaciones.`; });
            return r.json();
        })
        .then(asignaciones => {
            if (asignaciones.length === 0) {
                tbodyAsignaciones.innerHTML =
                    '<tr><td colspan="100%" class="text-center">No hay asignaciones registrados</td></tr>';
                return;
            }

            // Se muestran los datos de cada asignacion fila por fila (tr) en el tBody
            let tBody = "";
            asignaciones.forEach(p => {
                tBody += `
                    <tr>
                        <td>${p.id}</td>
                        <td>${p.nombre}</td>
                        <td>${p.apellidos}</td>
                        <td>${p.dni}</td>
                        <td>${p.telefono ? p.telefono : "-"}</td>
                        <td>${p.fechaNacimiento ? new Date(p.fechaNacimiento).toLocaleDateString() : "-"}</td>
                        <td style="max-width: 250px">${p.historial ? p.historial : "-"}</td>
                        <td>${p.medico ? p.medico.nombre : "-"}</td>
                        <td>${p.activo ? '<span class="badge text-bg-success">Activo</span>' : '<span class="badge text-bg-danger">Inactivo</span>'}</td>
                        <td>${p.fechaCreacion ? new Date(p.fechaCreacion).toLocaleString() : "-"}</td>
                        <td> 
                            <div class="d-flex gap-2 col-9"> 
                                <button class="btn btn-sm btn-outline-primary col-3" 
                                    onclick="cargarDialogEditar(${p.id})">Editar</button> 
                                <button class="btn btn-sm btn-outline-success col-5" 
                                    onclick="cargarDialogAsignarMedico(${p.id})">Asignar médico</button> 
                                <button class="btn btn-sm btn-outline-warning col-3" 
                                    onclick="cambiarEstado(${p.id})">${p.activo ? "Desactivar" : "Activar"} </button> 
                                <button class="btn btn-sm btn-outline-danger col-3" 
                                    onclick="eliminarAsignacion(${p.id})">Eliminar</button> 
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

// Función de validación del DNI español
function validarDNI(dni) {
    dni = dni.toUpperCase().trim();
    const letras = "TRWAGMYFPDXBNJZSQVHLCKE";

    // Formato: 8 dígitos + 1 letra
    if (!/^\d{8}[A-Z]$/.test(dni)) return false;

    const numero = parseInt(dni.slice(0, 8), 10);
    const letraCorrecta = letras[numero % 23];

    return letraCorrecta === dni.charAt(8);
}


// CREAR NUEVO PACIENTE
// Al pulsar el botón 'Crear nuevo asignacion' se abre el modal (dialog) con el formulario
document.getElementById("btnCrearAsignacion").onclick = () => {
    dialogCrearAsignacion.showModal();
};

const msgCrearError = document.getElementById("msgCrearError");

// Al enviar el formulario se llama a la función crear asignacion
document.getElementById("formCrearAsignacion").onsubmit = e => {
    e.preventDefault();

    // Validación del DNI
    const dni = document.getElementById("dniCrear").value.trim();

    if (!validarDNI(dni)) {
        msgCrearError.textContent = "DNI inválido. Debe tener 8 dígitos y 1 letra";
        msgCrearError.classList.remove("d-none");
        return;
    }

    crearAsignacion();
};
// Función que envía al backend los datos del nuevo asignacion, se guardan y se recarga la tabla
function crearAsignacion() {
    fetch("/admin/asignaciones", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            nombre: document.getElementById("nombreCrear").value.trim(),
            apellidos: document.getElementById("apellidosCrear").value.trim(),
            dni: document.getElementById("dniCrear").value.trim().toUpperCase(),
            telefono: document.getElementById("telefonoCrear").value.trim(),
            fechaNacimiento: document.getElementById("fechaNacimientoCrear").value,
            historial: document.getElementById("historialCrear").value.trim(),
            activo: document.getElementById("activoCrear").value === "true"
        })
    })
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se ha podido crear el asignacion.`; });
            return r.json();
        })
        .then((asignacionCreado) => {
            dialogCrearAsignacion.close();
            cargarAsignaciones();
            cargarDialogAsignarMedico(asignacionCreado.id); // Se abre el dialog para asignar médico
        })
        .catch(e => {
            msgCrearError.textContent = e;
            msgCrearError.classList.remove("d-none");
        });
}

dialogCrearAsignacion.addEventListener("close", () => {
    document.getElementById("formCrearAsignacion").reset();
    msgCrearError.classList.add("d-none");
});


// EDITAR PACIENTE

// Al pulsar el botón editar se llama a esta función que obtiene los datos del asignacion por su id
function cargarDialogEditar(id) {
    fetch("/admin/asignaciones/" + id)
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se han podido cargar los datos del asignacion.`; });
            return r.json();
        })
        .then(p => {
            document.getElementById("idAsignacionEditado").value = p.id;
            document.getElementById("nombreEditar").value = p.nombre;
            document.getElementById("apellidosEditar").value = p.apellidos;
            document.getElementById("dniEditar").value = p.dni;
            document.getElementById("telefonoEditar").value = p.telefono ?? "";
            document.getElementById("fechaNacimientoEditar").value = p.fechaNacimiento ?? "";
            document.getElementById("historialEditar").value = p.historial ?? "";
            document.getElementById("activoEditar").value = p.activo;
            dialogEditarAsignacion.showModal();
        })
        .catch((error) =>
            alert(error)
        );
}

// Al enviar el formulario se llama a la función editar asignacion
document.getElementById("formEditarAsignacion").onsubmit = e => {
    e.preventDefault();

    // Validación del DNI
    const dni = document.getElementById("dniEditar").value.trim();

    if (!validarDNI(dni)) {
        alert("DNI inválido. Debe tener 8 dígitos y 1 letra")
        return;
    }

    editarAsignacion();
};

function editarAsignacion() {
    const id = document.getElementById("idAsignacionEditado").value;

    fetch("/admin/asignaciones/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            nombre: document.getElementById("nombreEditar").value.trim(),
            apellidos: document.getElementById("apellidosEditar").value.trim(),
            dni: document.getElementById("dniEditar").value.trim().toUpperCase(),
            telefono: document.getElementById("telefonoEditar").value.trim(),
            fechaNacimiento: document.getElementById("fechaNacimientoEditar").value,
            historial: document.getElementById("historialEditar").value.trim(),
            activo: document.getElementById("activoEditar").value === "true"
        })
    })
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status} No se ha podido editar el asignacion.`; });
            dialogEditarAsignacion.close();
            cargarAsignaciones();
        })
        .catch((error) =>
            alert(error)
        );
}

// MODIFICAR ESTADO PACIENTE (Interruptor)
// Se hace un fetch (metodo PUT) al metodo que acciona el interruptor
// En el backend, el asignacion cambia al estado contrario (activo -> inactivo, inactivo -> activo)
function cambiarEstado(id) {
    fetch("/admin/asignaciones/" + id + "/estado", { method: "PUT" })
        .then(cargarAsignaciones)
        .catch(() => mostrarError("Error al modificar estado"));
}

// ELIMINAR PACIENTE
function eliminarAsignacion(id) {
    // Se pide confirmación
    if (!confirm(`¿Estás seguro/a de que quieres eliminar el asignacion con id ${id}?`)) return;

    // Se hace un fetch con metodo DELETE para eliminar el asignacion de la base de datos
    fetch("/admin/asignaciones/" + id, { method: "DELETE" })
        .then(cargarAsignaciones) // Se recarga la tabla
        .catch(() => mostrarError("Error al eliminar asignacion."));
}


// ASIGNAR MÉDICO

// (Acceso 1: desde el form de editar)
document.getElementById("btnCambiarMedico").onclick = () => {
    const id = document.getElementById("idAsignacionEditado").value;
    dialogEditarAsignacion.close();
    cargarDialogAsignarMedico(id);
};

// (Acceso 2: desde el botón del body)

// Al pulsar el botón asignar médico se llama a esta función que obtiene los datos del asignacion y los médicos disponibles
function cargarDialogAsignarMedico(id) {
    document.getElementById("idAsignacionAsignarMedico").value = id;
    document.getElementById("msgDialogMedicoError").classList.add("d-none");

    // Cargar médicos disponibles
    fetch("/admin/usuarios/medico", {
        method: "GET"
    })
        .then((r) => {
            if (!r.ok) {
                return r.json()
                    .then(data => { throw data.errorMsg || `Error ${r.status}: No se han podido cargar los médicos disponibles.`
                    });
            }
            return r.json();
        })
        .then((medicosDisponibles) => {
            // Cargar datos del asignacion
            return fetch("/admin/asignaciones/" + id, {
                method: "GET"
            })
                .then((r) => {
                    if (!r.ok)
                        return r.json()
                            .then(data => { throw data.errorMsg || `Error ${r.status}: No se han podido cargar los datos del asignacion.`
                            });
                    return r.json();
                })
                .then((asignacion) => {
                    // Mostrar nombre del asignacion en el dialog
                    document.getElementById("nombreAsignacionAsignarMedico").textContent = asignacion.nombre + " " + asignacion.apellidos;

                    // Obtener id del médico del asignacion
                    const medicoAsignacion = asignacion.medico ? asignacion.medico.id : null;

                    // Se genera el input tipo radio dinámicamente, con el médico del asignacion seleccionado por defecto
                    const radioMedicosDiv = document.getElementById("radioMedicosDiv");
                    let htmlRadioMedicos = "";

                    medicosDisponibles.forEach((medico) => {
                        htmlRadioMedicos += `
                            <div class="form-check">
                                <input class="form-check-input" type="radio" value="${medico.id}" name="radioMedicos"
                                       id="medico${medico.id}" ${medicoAsignacion === medico.id ? 'checked' : ''} ${medico.activo ? '' : 'disabled'}>
                                <label class="form-check-label" for="medico${medico.id}">${medico.nombre}</label>
                            </div>
                        `;
                    });

                    radioMedicosDiv.innerHTML = htmlRadioMedicos;
                    dialogAsignarMedico.showModal(); // Se muestra el dialog
                });
        })
        .catch((error) => {
            alert(error);
        });
}

// Al enviar el formulario se llama a la función asignar médico
document.getElementById("formAsignarMedico").onsubmit = (e) => {
    e.preventDefault();
    asignarMedico();
};

const msgDialogMedicoError = document.getElementById("msgDialogMedicoError");

// Función que envía el médico seleccionado al backend
function asignarMedico() {
    const id = document.getElementById("idAsignacionAsignarMedico").value;
    const medicoSeleccionado = document.querySelector("input[name='radioMedicos']:checked");

    // Se comprueba que se haya seleccionado un médico
    if (!medicoSeleccionado) {
        msgDialogMedicoError.textContent = "El asignacion debe tener un médico asignado.";
        msgDialogMedicoError.classList.remove("d-none");
        return;
    }

    fetch("/admin/asignaciones/medico/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(parseInt(medicoSeleccionado.value))
    }).then((r) => {
        if (!r.ok) return r.json()
            .then(data => {throw data.errorMsg || `Error: ${r.status}. No se ha podido asignar el nuevo médico.`
            });
        dialogAsignarMedico.close(); // Se cierra el dialog
        cargarAsignaciones(); // Se recargan los datos mostrados en la tabla
    }).catch((error) => {
        msgDialogMedicoError.textContent = error;
        msgDialogMedicoError.classList.remove("d-none");
    });
}
