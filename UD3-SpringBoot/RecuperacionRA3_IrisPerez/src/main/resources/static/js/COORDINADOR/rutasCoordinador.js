const tbodyRutas = document.getElementById("tBodyRutas");
const recuadroAlert = document.getElementById("recuadroAlert");

const dialogCrearRuta = document.getElementById("dialogCrearRuta");
const dialogEditarRuta = document.getElementById("dialogEditarRuta");
const dialogAsignarMedico = document.getElementById("dialogAsignarMedico");

// Mostrar nombre del usuario (admin) que esté usando el panel en el title
fetch("/user/datos")
    .then(r => r.json())
    .then(data => {
        document.title = "Gestión de rutas (" + data.nombre + ")";
    });

cargarRutas();

// CARGAR LOS DATOS DE LOS PACIENTES EN EL BODY DE LA TABLA
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
                    '<tr><td colspan="100%" class="text-center">No hay rutas registrados</td></tr>';
                return;
            }

            // Se muestran los datos de cada ruta fila por fila (tr) en el tBody
            let tBody = "";
            rutas.forEach(p => {
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
                                    onclick="eliminarRuta(${p.id})">Eliminar</button> 
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
// Al pulsar el botón 'Crear nuevo ruta' se abre el modal (dialog) con el formulario
document.getElementById("btnCrearRuta").onclick = () => {
    dialogCrearRuta.showModal();
};

const msgCrearError = document.getElementById("msgCrearError");

// Al enviar el formulario se llama a la función crear ruta
document.getElementById("formCrearRuta").onsubmit = e => {
    e.preventDefault();

    // Validación del DNI
    const dni = document.getElementById("dniCrear").value.trim();

    if (!validarDNI(dni)) {
        msgCrearError.textContent = "DNI inválido. Debe tener 8 dígitos y 1 letra";
        msgCrearError.classList.remove("d-none");
        return;
    }

    crearRuta();
};
// Función que envía al backend los datos del nuevo ruta, se guardan y se recarga la tabla
function crearRuta() {
    fetch("/admin/rutas", {
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
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se ha podido crear el ruta.`; });
            return r.json();
        })
        .then((rutaCreado) => {
            dialogCrearRuta.close();
            cargarRutas();
            cargarDialogAsignarMedico(rutaCreado.id); // Se abre el dialog para asignar médico
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


// EDITAR PACIENTE

// Al pulsar el botón editar se llama a esta función que obtiene los datos del ruta por su id
function cargarDialogEditar(id) {
    fetch("/admin/rutas/" + id)
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se han podido cargar los datos del ruta.`; });
            return r.json();
        })
        .then(p => {
            document.getElementById("idRutaEditado").value = p.id;
            document.getElementById("nombreEditar").value = p.nombre;
            document.getElementById("apellidosEditar").value = p.apellidos;
            document.getElementById("dniEditar").value = p.dni;
            document.getElementById("telefonoEditar").value = p.telefono ?? "";
            document.getElementById("fechaNacimientoEditar").value = p.fechaNacimiento ?? "";
            document.getElementById("historialEditar").value = p.historial ?? "";
            document.getElementById("activoEditar").value = p.activo;
            dialogEditarRuta.showModal();
        })
        .catch((error) =>
            alert(error)
        );
}

// Al enviar el formulario se llama a la función editar ruta
document.getElementById("formEditarRuta").onsubmit = e => {
    e.preventDefault();

    // Validación del DNI
    const dni = document.getElementById("dniEditar").value.trim();

    if (!validarDNI(dni)) {
        alert("DNI inválido. Debe tener 8 dígitos y 1 letra")
        return;
    }

    editarRuta();
};

function editarRuta() {
    const id = document.getElementById("idRutaEditado").value;

    fetch("/admin/rutas/" + id, {
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
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status} No se ha podido editar el ruta.`; });
            dialogEditarRuta.close();
            cargarRutas();
        })
        .catch((error) =>
            alert(error)
        );
}

// MODIFICAR ESTADO PACIENTE (Interruptor)
// Se hace un fetch (metodo PUT) al metodo que acciona el interruptor
// En el backend, el ruta cambia al estado contrario (activo -> inactivo, inactivo -> activo)
function cambiarEstado(id) {
    fetch("/admin/rutas/" + id + "/estado", { method: "PUT" })
        .then(cargarRutas)
        .catch(() => mostrarError("Error al modificar estado"));
}

// ELIMINAR PACIENTE
function eliminarRuta(id) {
    // Se pide confirmación
    if (!confirm(`¿Estás seguro/a de que quieres eliminar el ruta con id ${id}?`)) return;

    // Se hace un fetch con metodo DELETE para eliminar el ruta de la base de datos
    fetch("/admin/rutas/" + id, { method: "DELETE" })
        .then(cargarRutas) // Se recarga la tabla
        .catch(() => mostrarError("Error al eliminar ruta."));
}


// ASIGNAR MÉDICO

// (Acceso 1: desde el form de editar)
document.getElementById("btnCambiarMedico").onclick = () => {
    const id = document.getElementById("idRutaEditado").value;
    dialogEditarRuta.close();
    cargarDialogAsignarMedico(id);
};

// (Acceso 2: desde el botón del body)

// Al pulsar el botón asignar médico se llama a esta función que obtiene los datos del ruta y los médicos disponibles
function cargarDialogAsignarMedico(id) {
    document.getElementById("idRutaAsignarMedico").value = id;
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
            // Cargar datos del ruta
            return fetch("/admin/rutas/" + id, {
                method: "GET"
            })
                .then((r) => {
                    if (!r.ok)
                        return r.json()
                            .then(data => { throw data.errorMsg || `Error ${r.status}: No se han podido cargar los datos del ruta.`
                            });
                    return r.json();
                })
                .then((ruta) => {
                    // Mostrar nombre del ruta en el dialog
                    document.getElementById("nombreRutaAsignarMedico").textContent = ruta.nombre + " " + ruta.apellidos;

                    // Obtener id del médico del ruta
                    const medicoRuta = ruta.medico ? ruta.medico.id : null;

                    // Se genera el input tipo radio dinámicamente, con el médico del ruta seleccionado por defecto
                    const radioMedicosDiv = document.getElementById("radioMedicosDiv");
                    let htmlRadioMedicos = "";

                    medicosDisponibles.forEach((medico) => {
                        htmlRadioMedicos += `
                            <div class="form-check">
                                <input class="form-check-input" type="radio" value="${medico.id}" name="radioMedicos"
                                       id="medico${medico.id}" ${medicoRuta === medico.id ? 'checked' : ''} ${medico.activo ? '' : 'disabled'}>
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
    const id = document.getElementById("idRutaAsignarMedico").value;
    const medicoSeleccionado = document.querySelector("input[name='radioMedicos']:checked");

    // Se comprueba que se haya seleccionado un médico
    if (!medicoSeleccionado) {
        msgDialogMedicoError.textContent = "El ruta debe tener un médico asignado.";
        msgDialogMedicoError.classList.remove("d-none");
        return;
    }

    fetch("/admin/rutas/medico/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(parseInt(medicoSeleccionado.value))
    }).then((r) => {
        if (!r.ok) return r.json()
            .then(data => {throw data.errorMsg || `Error: ${r.status}. No se ha podido asignar el nuevo médico.`
            });
        dialogAsignarMedico.close(); // Se cierra el dialog
        cargarRutas(); // Se recargan los datos mostrados en la tabla
    }).catch((error) => {
        msgDialogMedicoError.textContent = error;
        msgDialogMedicoError.classList.remove("d-none");
    });
}
