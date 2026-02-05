var tbodyPacientes = document.getElementById("tBodyPacientes");
var recuadroAlert = document.getElementById("recuadroAlert");

var dialogCrearPaciente = document.getElementById("dialogCrearPaciente");
var dialogEditarPaciente = document.getElementById("dialogEditarPaciente");
var dialogCambiarPassword = document.getElementById("dialogCambiarPassword");
var dialogAsignarRoles = document.getElementById("dialogAsignarRoles");

// Mostrar nombre del médico en el title
fetch("/user/datos")
    .then((r) => {
        return r.json();
    })
    .then((data) => {
        document.title = "Gestión de pacientes (" + data.nombre + ")";
    });

cargarPacientes();

// CARGAR LOS DATOS DE LOS PACIENTES EN EL BODY DE LA TABLA
function cargarPacientes() {
    // Texto que aparece mientras cargan
    tbodyPacientes.innerHTML = '' + '<tr><td colspan="100%" class="text-center">Cargando pacientes...</td></tr>';

    // Fetch que obtiene los datos de los pacientes y los muestra
    fetch("/admin/pacientes", {
        method: "GET",
    }).then((response) => {
        if (!response.ok) mostrarError("Error al obtener los datos de los pacientes");
        return response.json();
    }).then((pacientes) => {
        if (pacientes.length === 0) {
            tbodyPacientes.innerHTML = '<tr><td colspan="100%" class="text-center">No hay pacientes registrados</td></tr>';
            return;
        }
        // Se muestran los datos de cada paciente fila por fila (tr) en el tBody
        var tBody = "";
        pacientes.forEach(p => {
            tBody += `
            <tr>
                <td>${p.id}</td>
                <td>${p.nombre}</td>
                <td>${p.apellidos}</td>
                <td>${p.dni}</td>
                <td>${p.telefono}</td>
                <td>${mostrarFecha(p.fechaNacimiento)}</td>
                <td>${p.historial}</td>
                <td>${p.medico}</td>
                <td>${mostrarEstado(p.activo)}</td>
                <td>${mostrarFecha(p.fechaCreacion)}</td>
                <!-- Botones para acciones CRUD -->
                <td>
                    <div class="d-flex gap-2 w-100">
                        <button class="btn btn-sm btn-outline-primary flex-fill"
                            onclick="cargarDialogEditar(${p.id})">Editar</button>
                        <button class="btn btn-sm btn-outline-success flex-fill" 
                            onclick="cargarDialogAsignarRoles(${p.id})">Asignar roles</button>
                        <button class="btn btn-sm btn-outline-warning flex-fill"
                                onclick="cambiarEstado(${p.id})">${p.activo ? "Desactivar" : "Activar"}
                        </button>
                        <button class="btn btn-sm btn-outline-danger flex-fill"
                                onclick="eliminarPaciente(${p.id})">Eliminar</button>
                    </div>
                </td>
            </tr>
        `;
        });
        tbodyPacientes.innerHTML = tBody;
    })
    .catch(() => {
        mostrarError("Error al cargar la lista de pacientes.");
    })
}

function mostrarEstado(activo) {
    if (activo) {
        return '<span class="badge text-bg-success">Activo</span>';
    }
    return '<span class="badge text-bg-danger">Inactivo</span>';
}

function mostrarFecha(fecha) {
    return fecha ? new Date(fecha).toLocaleString() : "-";
}

// Se muestra el error correspondiente en el recuadro durante 3 segs
function mostrarError(msg) {
    // Mostrar
    recuadroAlert.textContent = msg;
    recuadroAlert.classList.remove("d-none");

    // Ocultar
    setTimeout(() => {
        recuadroAlert.textContent = "";
        recuadroAlert.classList.add("d-none");
    }, 3000);
}


// CREAR NUEVO PACIENTE

// Al pulsar el botón 'Crear nuevo paciente' se abre el modal (dialog) con el formulario
document.getElementById("btnCrearPaciente").onclick = () => {
    dialogCrearPaciente.showModal();
};

var msgCrearError = document.getElementById("msgCrearError");

// Al enviar el formulario se llama a la función crear paciente
document.getElementById("formCrearPaciente").onsubmit = (e) => {
    e.preventDefault();

    // Validación del DNI

    var dni = document.getElementById("dniCrear").value.trim();

    if (!validarDNI(dni)) {
        msgCrearError.textContent = "DNI inválido. Debe tener 8 dígitos y 1 letra";
        msgCrearError.classList.remove("d-none");
        return;
    }

    // Si el dni es válido, se llama a la función para crear el paciente
    crearPaciente(dni);
};

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

// Función que envía al backend los datos del nuevo paciente, se guardan y se recarga la tabla
function crearPaciente(dni) {
    fetch("/admin/pacientes", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            nombre: document.getElementById("nombreCrear").value.trim(),
            apellidos: document.getElementById("apellidosCrear").value.trim(),
            username: document.getElementById("usernameCrear").value.trim(),
            email: document.getElementById("emailCrear").value.trim(),
            dni: dni.toUpperCase().trim(),
            password: document.getElementById("passwordCrear").value.trim(),
        })
    })
        .then((r) => {
            if (!r.ok) // Intentar leer mensaje de error del backend
                return r.json().then((data) => {
                    throw data.msg;
                }).catch(() => {
                    // Si no llega ningún JSON con mensaje de error personalizado
                    throw new Error(`Error ${r.status}: ${r.statusText}`);
                });
            return r.json();
        }).then(pacienteCreado => {
            dialogCrearPaciente.close(); // se cierra el dialog (pop-up)
            cargarPacientes(); // se recargan los datos mostrados en la tabla
            cargarDialogAsignarRoles(pacienteCreado.id); // Se abre el dialog para asignar roles
        })
        .catch((error) => {
            msgCrearError.textContent = error;
            msgCrearError.classList.remove("d-none");
        });
}

dialogCrearPaciente.addEventListener("close", () => {
    document.getElementById("formCrearPaciente").reset();
    msgCrearError.classList.add("d-none");
});

// EDITAR PACIENTE

// Al pulsar el botón editar se llama a esta función que obtiene los datos del paciente por su id
function cargarDialogEditar(id) {
    fetch("/admin/pacientes/" + id)
        .then((r) => {
            if (!r.ok) throw new Error(`Error ${r.status}: ${r.statusText}`);
            return r.json();
        })
        .then((p) => {
            document.getElementById("pacientePassEditadaId").value = p.id; // Por si pulsa cambiar contraseña
            document.getElementById("idPacienteEditado").value = p.id;
            document.getElementById("nombreEditar").value = p.nombre;
            document.getElementById("apellidosEditar").value = p.apellidos;
            document.getElementById("usernameEditar").value = p.username;
            document.getElementById("emailEditar").value = p.email;
            document.getElementById("dniEditar").value = p.dni;
            document.getElementById("activoEditar").value = p.activo
            dialogEditarPaciente.showModal(); // Una vez cargados los datos se muestra el dialog
        })
        .catch(() => {
            alert("Error al cargar los datos del paciente.");
        });
}


// Al enviar el formulario se llama a la función editar paciente
document.getElementById("formEditarPaciente").onsubmit = (e) => {
    e.preventDefault();

    var dni = document.getElementById("dniEditar").value;

    if (!validarDNI(dni)) {
        alert("DNI inválido. Debe tener 8 dígitos y 1 letra");
        return;
    }

    // Se obtiene el dni del paciente en el que se clicó el botón
    var id = document.getElementById("idPacienteEditado").value;

    editarPaciente(id);
};

// Se hace un fetch (metodo PUT) que envía los datos actualizados al backend.
// En el backend se sustituyen los datos originales del paciente por los modificados.
function editarPaciente(id) {
    return fetch("/admin/pacientes/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            nombre: document.getElementById("nombreEditar").value.trim(),
            apellidos: document.getElementById("apellidosEditar").value.trim(),
            username: document.getElementById("usernameEditar").value.trim(),
            email: document.getElementById("emailEditar").value.trim(),
            dni: document.getElementById("dniEditar").value.toUpperCase().trim(),
            activo: document.getElementById("activoEditar").value === "true"
            // === "true" es true (boolean) si coincide, y si no false (boolean)
            // de esta manera conseguimos que devuelva un booleano y no un string
        })
    }).then((r) => {
        if (!r.ok) // Intentar leer mensaje de error del backend
            return r.json().then((data) => {
                throw data.msg;
            }).catch(() => {
                // Si no llega ningún JSON con mensaje de error personalizado
                throw new Error(`Error ${r.status}: ${r.statusText}`);
            });
        dialogEditarPaciente.close(); // Se cierra el dialog
        cargarPacientes(); // Se recargan los datos mostrados en la tabla
    })
    .catch((error) => {
        alert("Error al editar los datos del paciente. " + error);
    });
}


// CAMBIAR CONTRASEÑA

// Se muestra el dialog al pulsar el boton 'Cambiar contraseña'
document.getElementById("btnCambiarPassword").onclick = () => {
    dialogEditarPaciente.close() // Se oculta el dialog editar paciente
    msgPassError.classList.add("d-none");
    dialogCambiarPassword.showModal();
}

var msgPassError = document.getElementById("msgPassError");

// Al enviar el formulario se comprueba que la contraseña nueva coincida en ambos campos
// si coinciden, se llama a la funcion cambiarPassword
document.getElementById("formCambiarPassword").onsubmit = (e) => {
    e.preventDefault();

    var passNueva = document.getElementById("passNueva").value.trim();
    var passNuevaConfirm = document.getElementById("passNuevaConfirm").value.trim();

    // Si no coinciden la contraseña nueva y la confirmación se muestra un error
    if (passNueva !== passNuevaConfirm) {
        msgPassError.textContent = "Las contraseñas no coinciden.";
        msgPassError.classList.remove("d-none");
    } else {
        msgPassError.classList.add("d-none");
        cambiarPassword(passNueva);
    }
};

function cambiarPassword(passNueva) {
    var id = document.getElementById("pacientePassEditadaId").value;
    var passActual = document.getElementById("passActual").value;

    fetch("/admin/pacientes/" + id + "/password", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            id: id,
            passwordActual: passActual,
            passwordNueva: passNueva
        })
    })
        .then((r) => {
            if (!r.ok) {
                // Intentar leer mensaje de error del backend
                return r.json().then((data) => {
                    throw data.mensaje;
                }).catch(() => {
                    // Si no llega ningún JSON con mensaje de error se usa uno genérico
                    throw "Error. No se ha podido cambiar la contraseña.";
                });
            }
            // Se muestra un mensaje de éxito
            alert("Contraseña modificada con éxito :)")
            dialogCambiarPassword.close(); // Se cierra el dialog
        })
        // Catch que recoge los errores con su mensaje y los muestra con msgPassError
        .catch((error) => {
            msgPassError.textContent = error;
            msgPassError.classList.remove("d-none");
        });
}


// MODIFICAR ESTADO PACIENTE (Interruptor)

// Se hace un fetch (metodo PUT) al metodo que acciona el interruptor
// En el backend, el paciente cambia al estado contrario (activo -> inactivo, inactivo -> activo)
function cambiarEstado(id) {
    fetch("/admin/pacientes/" + id + "/estado", { method: "PUT" })
        .then(() => {
            cargarPacientes(); // Se recargan los datos de la tabla
        }).catch(() => {
            mostrarError("Error al modificar el estado del paciente con id: " + id);
        });
}


// ELIMINAR PACIENTE

function eliminarPaciente(id) {
    // Se pide confirmación
    if (!confirm("¿Estás segur@ de que quieres eliminar este paciente?\nId: " + id)) return;

    // Se hace un fetch con metodo DELETE para eliminar el paciente de la base de datos
    fetch("/admin/pacientes/" + id, { method: "DELETE" })
        .then(() => {
            cargarPacientes(); // Se recarga la tabla
        })
        .catch(() => {
            alert("Error al eliminar el paciente con id: " + id);
        });
}

// ASIGNAR ROLES

// Al pulsar el botón asignar roles se llama a esta función que obtiene los datos del paciente y los roles disponibles
function cargarDialogAsignarRoles(id) {
    document.getElementById("idPacienteAsignarRoles").value = id;
    document.getElementById("msgRolesError").classList.add("d-none");

    // Cargar roles existentes
    fetch("/admin/roles")
        .then((r) => {
            if (!r.ok) throw new Error("Error al cargar roles.");
            return r.json();
        })
        .then((rolesDisponibles) => {
            // Cargar datos del paciente con sus roles actuales
            return fetch("/admin/pacientes/" + id)
                .then((r) => {
                    if (!r.ok) throw new Error("Error al cargar paciente");
                    return r.json();
                })
                .then((paciente) => {
                    // Mostrar nombre del paciente en el dialog
                    document.getElementById("nombrePacienteAsignarRoles").textContent = paciente.nombre + " " + paciente.apellidos + " (" + paciente.username + "). Id: " + paciente.id;

                    // Obtener los ids de los roles que tiene el paciente
                    var rolesPaciente = paciente.roles.map(r => r.id);

                    // Se genera el checkbox dinámicamente, seleccionando los roles que ya tiene asignados el paciente
                    var checkboxRoles = document.getElementById("checkboxRoles");
                    var htmlCheckboxRoles = "";

                    rolesDisponibles.forEach((rol) => {
                        htmlCheckboxRoles += `
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" value="${rol.id}"
                                       id="rol${rol.id}" ${(rolesPaciente.includes(rol.id) ? 'checked' : '')}>
                                <label class="form-check-label" for="rol${rol.id}">${rol.nombre}</label>
                            </div>
                        `;
                    });

                    checkboxRoles.innerHTML = htmlCheckboxRoles;
                    dialogAsignarRoles.showModal(); // Se muestra el dialog
                });
        })
        .catch(() => {
            alert("Error al cargar datos de roles.");
        });
}

// Al enviar el formulario se llama a la función asignar roles
document.getElementById("formAsignarRoles").onsubmit = (e) => {
    e.preventDefault();
    asignarRoles();
};

var msgRolesError = document.getElementById("msgRolesError");

// Función que envía los roles seleccionados al backend
function asignarRoles() {
    var id = document.getElementById("idPacienteAsignarRoles").value;
    var checkboxMarcados = document.querySelectorAll("#checkboxRoles input[type='checkbox']:checked");
    var rolesSeleccionados = [];

    checkboxMarcados.forEach((cb) => {
        rolesSeleccionados.push(parseInt(cb.value));
    });

    // Se comprueba que se haya seleccionado al menos un rol
    if (rolesSeleccionados.length === 0) {
        msgRolesError.textContent = "El paciente debe tener al menos un rol.";
        msgRolesError.classList.remove("d-none");
        return;
    }

    fetch("/admin/pacientes/" + id + "/roles", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(rolesSeleccionados)
    })
        .then((r) => {
            if (!r.ok) throw new Error("Error al actualizar roles");
            dialogAsignarRoles.close(); // Se cierra el dialog
            cargarPacientes(); // Se recargan los datos mostrados en la tabla
        })
        .catch(() => {
            msgRolesError.textContent = "Error al actualizar los roles del paciente.";
            msgRolesError.classList.remove("d-none");
        });
}
