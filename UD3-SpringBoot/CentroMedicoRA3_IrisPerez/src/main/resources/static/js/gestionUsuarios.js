var tbodyUsuarios = document.getElementById("tBodyUsuarios");
var recuadroAlert = document.getElementById("recuadroAlert");

var dialogCrearUsuario = document.getElementById("dialogCrearUsuario");
var dialogEditarUsuario = document.getElementById("dialogEditarUsuario");
var dialogCambiarPassword = document.getElementById("dialogCambiarPassword");
var dialogAsignarRoles = document.getElementById("dialogAsignarRoles");

// Mostrar nombre del admin en el title
fetch("/user/datos")
    .then((r) => {
        return r.json();
    })
    .then((data) => {
        document.title = "Gestión de usuarios (" + data.nombre + ")";
    });

cargarUsuarios();

// CARGAR LOS DATOS DE LOS USUARIOS EN EL BODY DE LA TABLA
function cargarUsuarios() {
    // Texto que aparece mientras cargan
    tbodyUsuarios.innerHTML = '' + '<tr><td colspan="100%" class="text-center">Cargando usuarios...</td></tr>';

    // Fetch que obtiene los datos de los usuarios y los muestra
    fetch("/admin/usuarios", {
        method: "GET",
    }).then((response) => {
        if (!response.ok) mostrarError("Error al obtener los datos de los usuarios");
        return response.json();
    }).then((usuarios) => {
        if (usuarios.length === 0) {
            tbodyUsuarios.innerHTML = '<tr><td colspan="100%" class="text-center">No hay usuarios registrados</td></tr>';
            return;
        }
        // Se muestran los datos de cada usuario fila por fila (tr) en el tBody
        var tBody = "";
        usuarios.forEach(u => {
            tBody += `
            <tr>
                <td>${u.id}</td>
                <td>${u.username}</td>
                <td>${u.email}</td>
                <td>${u.nombre}</td>
                <td>${mostrarEstado(u.activo)}</td>
                <td>${mostrarFecha(u.fechaCreacion)}</td>
                <td>${mostrarRoles(u.roles)}</td>
                <td>${mostrarPacientes(u.pacientes)}</td>
                <!-- Botones para acciones CRUD -->
                <td>
                    <div class="d-flex gap-2 w-100">
                        <button class="btn btn-sm btn-outline-primary flex-fill"
                            onclick="cargarDialogEditar(${u.id})">Editar</button>
                        <button class="btn btn-sm btn-outline-success flex-fill" 
                            onclick="cargarDialogAsignarRoles(${u.id})">Asignar roles</button>
                        <button class="btn btn-sm btn-outline-warning flex-fill"
                            onclick="cambiarEstado(${u.id})">${u.activo ? "Desactivar" : "Activar"}
                        </button>
                        <button class="btn btn-sm btn-outline-danger flex-fill"
                            onclick="eliminarUsuario(${u.id})">Eliminar</button>
                    </div>
                </td>
            </tr>
        `;
        });
        tbodyUsuarios.innerHTML = tBody;
    })
    .catch(() => {
        mostrarError("Error al cargar la lista de usuarios.");
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

function mostrarRoles(roles) {
    if (!roles || roles.length === 0) return "-";
    return Array.from(roles).map(rol => rol.nombre).join(', ');
}

function mostrarPacientes(pacientes) {
    if (!pacientes || pacientes.length === 0) return "Sin pacientes";
    return Array.from(pacientes).map(paciente => paciente.nombre + " " + paciente.apellidos).join(', ');
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


// CREAR NUEVO USUARIO

// Al pulsar el botón 'Crear nuevo usuario' se abre el modal (dialog) con el formulario
document.getElementById("btnCrearUsuario").onclick = () => {
    dialogCrearUsuario.showModal();
};

var msgCrearError = document.getElementById("msgCrearError");

// Al enviar el formulario se llama a la función crear usuario
document.getElementById("formCrearUsuario").onsubmit = (e) => {
    e.preventDefault();

    // Validación del DNI

    var dni = document.getElementById("dniCrear").value.trim();

    if (!validarDNI(dni)) {
        msgCrearError.textContent = "DNI inválido. Debe tener 8 dígitos y 1 letra";
        msgCrearError.classList.remove("d-none");
        return;
    }

    // Si el dni es válido, se llama a la función para crear el usuario
    crearUsuario(dni);
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

// Función que envía al backend los datos del nuevo usuario, se guardan y se recarga la tabla
function crearUsuario(dni) {
    fetch("/admin/usuarios", {
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
        }).then(usuarioCreado => {
            dialogCrearUsuario.close(); // se cierra el dialog (pop-up)
            cargarUsuarios(); // se recargan los datos mostrados en la tabla
            cargarDialogAsignarRoles(usuarioCreado.id); // Se abre el dialog para asignar roles
        })
        .catch((error) => {
            msgCrearError.textContent = error;
            msgCrearError.classList.remove("d-none");
        });
}

dialogCrearUsuario.addEventListener("close", () => {
    document.getElementById("formCrearUsuario").reset();
    msgCrearError.classList.add("d-none");
});

// EDITAR USUARIO

// Al pulsar el botón editar se llama a esta función que obtiene los datos del usuario por su id
function cargarDialogEditar(id) {
    fetch("/admin/usuarios/" + id)
        .then((r) => {
            if (!r.ok) throw new Error(`Error ${r.status}: ${r.statusText}`);
            return r.json();
        })
        .then((u) => {
            document.getElementById("usuarioPassEditadaId").value = u.id; // Por si pulsa cambiar contraseña
            document.getElementById("idUsuarioEditado").value = u.id;
            document.getElementById("nombreEditar").value = u.nombre;
            document.getElementById("apellidosEditar").value = u.apellidos;
            document.getElementById("usernameEditar").value = u.username;
            document.getElementById("emailEditar").value = u.email;
            document.getElementById("dniEditar").value = u.dni;
            document.getElementById("activoEditar").value = u.activo
            dialogEditarUsuario.showModal(); // Una vez cargados los datos se muestra el dialog
        })
        .catch(() => {
            alert("Error al cargar los datos del usuario.");
        });
}


// Al enviar el formulario se llama a la función editar usuario
document.getElementById("formEditarUsuario").onsubmit = (e) => {
    e.preventDefault();

    var dni = document.getElementById("dniEditar").value;

    if (!validarDNI(dni)) {
        alert("DNI inválido. Debe tener 8 dígitos y 1 letra");
        return;
    }

    // Se obtiene el dni del usuario en el que se clicó el botón
    var id = document.getElementById("idUsuarioEditado").value;

    editarUsuario(id);
};

// Se hace un fetch (metodo PUT) que envía los datos actualizados al backend.
// En el backend se sustituyen los datos originales del usuario por los modificados.
function editarUsuario(id) {
    return fetch("/admin/usuarios/" + id, {
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
        dialogEditarUsuario.close(); // Se cierra el dialog
        cargarUsuarios(); // Se recargan los datos mostrados en la tabla
    })
    .catch((error) => {
        alert("Error al editar los datos del usuario. " + error);
    });
}


// CAMBIAR CONTRASEÑA

// Se muestra el dialog al pulsar el boton 'Cambiar contraseña'
document.getElementById("btnCambiarPassword").onclick = () => {
    dialogEditarUsuario.close() // Se oculta el dialog editar usuario
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
    var id = document.getElementById("usuarioPassEditadaId").value;
    var passActual = document.getElementById("passActual").value;

    fetch("/admin/usuarios/" + id + "/password", {
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


// MODIFICAR ESTADO USUARIO (Interruptor)

// Se hace un fetch (metodo PUT) al metodo que acciona el interruptor
// En el backend, el usuario cambia al estado contrario (activo -> inactivo, inactivo -> activo)
function cambiarEstado(id) {
    fetch("/admin/usuarios/" + id + "/estado", { method: "PUT" })
        .then(() => {
            cargarUsuarios(); // Se recargan los datos de la tabla
        }).catch(() => {
            mostrarError("Error al modificar el estado del usuario con id: " + id);
        });
}


// ELIMINAR USUARIO

function eliminarUsuario(id) {
    // Se pide confirmación
    if (!confirm("¿Estás segur@ de que quieres eliminar este usuario?\nId: " + id)) return;

    // Se hace un fetch con metodo DELETE para eliminar el usuario de la base de datos
    fetch("/admin/usuarios/" + id, { method: "DELETE" })
        .then(() => {
            cargarUsuarios(); // Se recarga la tabla
        })
        .catch(() => {
            alert("Error al eliminar el usuario con id: " + id);
        });
}

// ASIGNAR ROLES

// Al pulsar el botón asignar roles se llama a esta función que obtiene los datos del usuario y los roles disponibles
function cargarDialogAsignarRoles(id) {
    document.getElementById("idUsuarioAsignarRoles").value = id;
    document.getElementById("msgRolesError").classList.add("d-none");

    // Cargar roles existentes
    fetch("/admin/usuarios/roles")
        .then((r) => {
            if (!r.ok) throw new Error("Error al cargar roles.");
            return r.json();
        })
        .then((rolesDisponibles) => {
            // Cargar datos del usuario con sus roles actuales
            return fetch("/admin/usuarios/" + id)
                .then((r) => {
                    if (!r.ok) throw new Error("Error al cargar usuario");
                    return r.json();
                })
                .then((usuario) => {
                    // Mostrar nombre del usuario en el dialog
                    document.getElementById("nombreUsuarioAsignarRoles").textContent = usuario.nombre + " " + usuario.apellidos + " (" + usuario.username + "). Id: " + usuario.id;

                    // Obtener los ids de los roles que tiene el usuario
                    var rolesUsuario = usuario.roles.map(r => r.id);

                    // Se genera el checkbox dinámicamente, seleccionando los roles que ya tiene asignados el usuario
                    var checkboxRoles = document.getElementById("checkboxRoles");
                    var htmlCheckboxRoles = "";

                    rolesDisponibles.forEach((rol) => {
                        htmlCheckboxRoles += `
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" value="${rol.id}"
                                       id="rol${rol.id}" ${(rolesUsuario.includes(rol.id) ? 'checked' : '')}>
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
    var id = document.getElementById("idUsuarioAsignarRoles").value;
    var checkboxMarcados = document.querySelectorAll("#checkboxRoles input[type='checkbox']:checked");
    var rolesSeleccionados = [];

    checkboxMarcados.forEach((cb) => {
        rolesSeleccionados.push(parseInt(cb.value));
    });

    // Se comprueba que se haya seleccionado al menos un rol
    if (rolesSeleccionados.length === 0) {
        msgRolesError.textContent = "El usuario debe tener al menos un rol.";
        msgRolesError.classList.remove("d-none");
        return;
    }

    fetch("/admin/usuarios/roles/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(rolesSeleccionados)
    })
        .then((r) => {
            if (!r.ok) throw new Error("Error al actualizar roles");
            dialogAsignarRoles.close(); // Se cierra el dialog
            cargarUsuarios(); // Se recargan los datos mostrados en la tabla
        })
        .catch(() => {
            msgRolesError.textContent = "Error al actualizar los roles del usuario.";
            msgRolesError.classList.remove("d-none");
        });
}
