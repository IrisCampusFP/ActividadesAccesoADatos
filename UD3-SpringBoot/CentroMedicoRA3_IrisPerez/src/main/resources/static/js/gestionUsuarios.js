const tbodyUsuarios = document.getElementById("tBodyUsuarios");
const recuadroAlert = document.getElementById("recuadroAlert");
const dialogCrearUsuario = document.getElementById("dialogCrearUsuario");
const dialogEditarUsuario = document.getElementById("dialogEditarUsuario");
const dialogCambiarPassword = document.getElementById("dialogCambiarPassword");
const dialogAsignarRoles = document.getElementById("dialogAsignarRoles");

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
        method: "GET"
    }).then((response) => {
        if (!response.ok) return response.json()
            .then(data => { throw data.errorMsg || `Error ${response.status}: No se ha podido cargar la lista de usuarios.`
            })
        return response.json();
    }).then((usuarios) => {
        if (usuarios.length === 0) {
            tbodyUsuarios.innerHTML = '<tr><td colspan="100%" class="text-center">No hay usuarios registrados</td></tr>';
            return;
        }
        // Se muestran los datos de cada usuario fila por fila (tr) en el tBody
        let tBody = "";
        usuarios.forEach(u => {
            tBody += `
                <tr>
                    <td>${u.id}</td>
                    <td>${u.username}</td>
                    <td>${u.email}</td>
                    <td>${u.nombre}</td>
                    <td>${u.activo ? '<span class="badge text-bg-success">Activo</span>' : '<span class="badge text-bg-danger">Inactivo</span>'}</td>
                    <td>${u.fechaCreacion ? new Date(u.fechaCreacion).toLocaleString() : "-"}</td>
                    <td>${mostrarRoles(u.roles)}</td>
                    <!-- Botones para acciones CRUD -->
                    <td>
                        <div class="d-flex gap-2 col-11">
                            <button class="btn btn-sm btn-outline-primary col-3"
                                onclick="cargarDialogEditar(${u.id})">Editar</button>
                            <button class="btn btn-sm btn-outline-success col-3" 
                                onclick="cargarDialogAsignarRoles(${u.id})">Asignar roles</button>
                            <button class="btn btn-sm btn-outline-warning col-3"
                                onclick="cambiarEstado(${u.id})">${u.activo ? "Desactivar" : "Activar"}
                            </button>
                            <button class="btn btn-sm btn-outline-danger col-3"
                                onclick="eliminarUsuario(${u.id})">Eliminar</button>
                        </div>
                    </td>
                </tr>
            `;
        });
        tbodyUsuarios.innerHTML = tBody;
    })
    .catch((error) => {
        mostrarError(error);
    })
}

function mostrarRoles(roles) {
    if (!roles || roles.length === 0) return "-";
    return roles.map(rol => rol.nombre).join(', ');
}

// Se muestra el error correspondiente en el recuadro durante 3 segs
function mostrarError(errorMsg) {
    // Mostrar
    recuadroAlert.textContent = errorMsg;
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

const msgCrearError = document.getElementById("msgCrearError");

// Al enviar el formulario se llama a la función crear usuario
document.getElementById("formCrearUsuario").onsubmit = (e) => {
    e.preventDefault();
    crearUsuario();
};



// Función que envía al backend los datos del nuevo usuario, se guardan y se recarga la tabla
function crearUsuario() {
    fetch("/admin/usuarios", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            username: document.getElementById("usernameCrear").value.trim(),
            email: document.getElementById("emailCrear").value.trim(),
            password: document.getElementById("passwordCrear").value.trim(),
            nombre: document.getElementById("nombreCrear").value.trim(),
            activo: document.getElementById("activoCrear").value === "true"
        })
    })
        .then((r) => {
            if (!r.ok) return r.json()
                .then((data) => {
                    throw data.errorMsg || `Error ${r.status}: No se ha podido crear el usuario.`
                })
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
    fetch("/admin/usuarios/" + id, {
        method: "GET"
    }).then((r) => {
        if (!r.ok) return r.json()
            .then((data) => {
                throw data.errorMsg || `Error ${r.status}: No se han podido cargar los datos del usuario.`
            });
        return r.json();
    })
    .then((u) => {
        document.getElementById("usuarioPassEditadaId").value = u.id; // Guardo el id del usuario por si selecciona cambiar contraseña

        document.getElementById("idUsuarioEditado").value = u.id;
        document.getElementById("usernameEditar").value = u.username;
        document.getElementById("emailEditar").value = u.email;
        document.getElementById("nombreEditar").value = u.nombre;
        document.getElementById("activoEditar").value = u.activo
        dialogEditarUsuario.showModal(); // Una vez cargados los datos se muestra el dialog
    })
    .catch((error) => {
        alert(error);
    });
}

// Al enviar el formulario se llama a la función editar usuario
document.getElementById("formEditarUsuario").onsubmit = (e) => {
    e.preventDefault();

    // Se obtiene el id del usuario en el que se clicó el botón
    const id = document.getElementById("idUsuarioEditado").value;

    editarUsuario(id);
};

// Se hace un fetch (metodo PUT) que envía los datos actualizados al backend.
// En el backend se sustituyen los datos originales del usuario por los modificados.
function editarUsuario(id) {
    fetch("/admin/usuarios/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            username: document.getElementById("usernameEditar").value.trim(),
            email: document.getElementById("emailEditar").value.trim(),
            nombre: document.getElementById("nombreEditar").value.trim(),
            activo: document.getElementById("activoEditar").value === "true"
            // === "true" es true (boolean) si coincide, y si no false (boolean)
            // de esta manera conseguimos devolver un booleano y no un string
        })
    }).then((r) => {
        if (!r.ok) return r.json().then((data) => {
                throw data.errorMsg || `Error ${r.status}: No se ha podido actualizar usuario.`
            });
        dialogEditarUsuario.close(); // Se cierra el dialog
        cargarUsuarios(); // Se recargan los datos mostrados en la tabla
    })
    .catch((error) => {
        alert(error);
    });
}


// CAMBIAR CONTRASEÑA

const msgPassError = document.getElementById("msgPassError");


// Se muestra el dialog al pulsar el boton 'Cambiar contraseña'
document.getElementById("btnCambiarPassword").onclick = () => {
    dialogEditarUsuario.close() // Se oculta el dialog editar usuario
    msgPassError.classList.add("d-none");
    dialogCambiarPassword.showModal();
}


// Al enviar el formulario se comprueba que la contraseña nueva coincida en ambos campos
// si coinciden, se llama a la funcion cambiarPassword
document.getElementById("formCambiarPassword").onsubmit = (e) => {
    e.preventDefault();

    const passNueva = document.getElementById("passNueva").value.trim();
    const passNuevaConfirm = document.getElementById("passNuevaConfirm").value.trim();

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
    const id = document.getElementById("usuarioPassEditadaId").value;
    const passActual = document.getElementById("passActual").value;

    fetch("/admin/usuarios/" + id + "/password", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            passwordActual: passActual,
            passwordNueva: passNueva
        })
    })
        .then((r) => {
            if (!r.ok) {
                return r.json()
                    .then((data) => {
                        throw data.errorMsg || `Error ${r.status}: No se ha podido cambiar la contraseña.`
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
    if (!confirm("¿Estás seguro/a de que quieres eliminar este usuario?\nId: " + id)) return;

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
    fetch("/admin/usuarios/roles", {
        method: "GET"
    })
        .then((r) => {
            if (!r.ok) {
                return r.json()
                    .then(data => { throw data.errorMsg || `Error ${r.status}: No se han podido cargar roles existentes.`
                    });
            }
            return r.json();
        })
        .then((rolesDisponibles) => {
            // Cargar datos del usuario con sus roles actuales
            return fetch("/admin/usuarios/" + id)
                .then((r) => {
                    if (!r.ok)
                        return r.json()
                            .then(data => { throw data.errorMsg || `Error ${r.status}: No se ha podido cargar usuario.`
                        });
                    return r.json();
                })
                .then((usuario) => {
                    // Mostrar nombre del usuario en el dialog
                    document.getElementById("nombreUsuarioAsignarRoles").textContent = usuario.nombre + " " + usuario.apellidos + " (" + usuario.username + "). Id: " + usuario.id;

                    // Obtener los ids de los roles que tiene el usuario
                    const rolesUsuario = usuario.roles.map(r => r.id);

                    // Se genera el checkbox dinámicamente, seleccionando los roles que ya tiene asignados el usuario
                    const checkboxRoles = document.getElementById("checkboxRoles");
                    let htmlCheckboxRoles = "";

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
        .catch((error) => {
            alert(error);
        });
}

// Al enviar el formulario se llama a la función asignar roles
document.getElementById("formAsignarRoles").onsubmit = (e) => {
    e.preventDefault();
    asignarRoles();
};

const msgRolesError = document.getElementById("msgRolesError");

// Función que envía los roles seleccionados al backend
function asignarRoles() {
    const id = document.getElementById("idUsuarioAsignarRoles").value;
    const checkboxMarcados = document.querySelectorAll("#checkboxRoles input[type='checkbox']:checked");
    const rolesSeleccionados = [];

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
            if (!r.ok) return r.json()
                .then(data => {throw data.errorMsg || `Error: ${r.status}. No se han podido actualizar roles.`
                });
            dialogAsignarRoles.close(); // Se cierra el dialog
            cargarUsuarios(); // Se recargan los datos mostrados en la tabla
        })
        .catch((error) => {
            msgRolesError.textContent = error;
            msgRolesError.classList.remove("d-none");
        });
}
