var tbodyPacientes = document.getElementById("tBodyPacientes");
var recuadroAlert = document.getElementById("recuadroAlert");

var dialogCrearPaciente = document.getElementById("dialogCrearPaciente");
var dialogEditarPaciente = document.getElementById("dialogEditarPaciente");

// Mostrar nombre del usuario (médico) que esté usando el panel en el title
fetch("/user/datos")
    .then(r => r.json())
    .then(data => {
        document.title = "Listado Pacientes (" + data.nombre + ")";
    });

cargarPacientes();

// CARGAR LOS DATOS DE LOS PACIENTES EN EL BODY DE LA TABLA
function cargarPacientes() {
    // Texto que aparece mientras cargan
    tbodyPacientes.innerHTML =
        '<tr><td colspan="100%" class="text-center">Cargando pacientes...</td></tr>';

    // Fetch que obtiene los datos de los pacientes y los muestra
    fetch("/medico/pacientes")
        .then(r => {
            if (!r.ok) return r.json().then(data => { throw data.errorMsg || `Error ${r.status}: No se ha podido cargar la lista de pacientes.`; });
            return r.json();
        })
        .then(pacientes => {
            if (pacientes.length === 0) {
                tbodyPacientes.innerHTML =
                    '<tr><td colspan="100%" class="text-center">No hay pacientes registrados</td></tr>';
                return;
            }

            // Se muestran los datos de cada paciente fila por fila (tr) en el tBody
            let tBody = "";
            pacientes.forEach(p => {
                tBody += `
                    <tr>
                        <td>${p.id}</td>
                        <td>${p.nombre}</td>
                        <td>${p.apellidos}</td>
                        <td>${p.dni}</td>
                        <td>${p.telefono ? p.telefono : "-"}</td>
                        <td>${p.fechaNacimiento ? new Date(p.fechaNacimiento).toLocaleDateString() : "-"}</td>
                        <td style="max-width: 250px">${p.historial ? p.historial : "-"}</td>
                        <td>${p.activo ? '<span class="badge text-bg-success">Activo</span>' : '<span class="badge text-bg-danger">Inactivo</span>'}</td>
                        <td>${p.fechaCreacion ? new Date(p.fechaCreacion).toLocaleString() : "-"}</td>
                        <td> 
                            <div class="d-flex gap-2 col-11"> 
                                <button class="btn btn-sm btn-outline-primary col-6" 
                                    onclick="cargarDialogEditar(${p.id})">Editar</button> 
                                <button class="btn btn-sm btn-outline-warning col-6" 
                                    onclick="cambiarEstado(${p.id})">${p.activo ? "Desactivar" : "Activar"} </button> 
                            </div> 
                        </td>
                    </tr>
                `;
            });
            tbodyPacientes.innerHTML = tBody;
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
// Al pulsar el botón 'Crear nuevo paciente' se abre el modal (dialog) con el formulario
document.getElementById("btnCrearPaciente").onclick = () => {
    dialogCrearPaciente.showModal();
};

var msgCrearError = document.getElementById("msgCrearError");

// Al enviar el formulario se llama a la función crear paciente
document.getElementById("formCrearPaciente").onsubmit = e => {
    e.preventDefault();

    // Validación del DNI
    var dni = document.getElementById("dniCrear").value.trim();

    if (!validarDNI(dni)) {
        msgCrearError.textContent = "DNI inválido. Debe tener 8 dígitos y su letra correspondiente.";
        msgCrearError.classList.remove("d-none");
        return;
    }

    crearPaciente();
};
// Función que envía al backend los datos del nuevo paciente, se guardan y se recarga la tabla
function crearPaciente() {
    fetch("/medico/pacientes", {
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
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se ha podido crear el paciente.`; });
            return r.json();
        })
        .then(() => {
            dialogCrearPaciente.close();
            cargarPacientes();
        })
        .catch(e => {
            msgCrearError.textContent = e;
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
    fetch("/medico/pacientes/" + id)
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se han podido cargar los datos del paciente.`; });
            return r.json();
        })
        .then(p => {
            document.getElementById("idPacienteEditado").value = p.id;
            document.getElementById("nombreEditar").value = p.nombre;
            document.getElementById("apellidosEditar").value = p.apellidos;
            document.getElementById("dniEditar").value = p.dni;
            document.getElementById("telefonoEditar").value = p.telefono ?? "";
            document.getElementById("fechaNacimientoEditar").value = p.fechaNacimiento ?? "";
            document.getElementById("historialEditar").value = p.historial ?? "";
            document.getElementById("activoEditar").value = p.activo;
            dialogEditarPaciente.showModal();
        })
        .catch((error) =>
            alert(error)
        );
}

// Al enviar el formulario se llama a la función editar paciente
document.getElementById("formEditarPaciente").onsubmit = e => {
    e.preventDefault();

    // Validación del DNI
    var dni = document.getElementById("dniEditar").value.trim();

    if (!validarDNI(dni)) {
        alert("DNI inválido. Debe tener 8 dígitos y su letra correspondiente.")
        return;
    }

    editarPaciente();
};

function editarPaciente() {
    const id = document.getElementById("idPacienteEditado").value;

    fetch("/medico/pacientes/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            nombre: document.getElementById("nombreEditar").value.trim(),
            apellidos: document.getElementById("apellidosEditar").value.trim(),
            dni: document.getElementById("dniEditar").value.trim().toUpperCase(),
            telefono: document.getElementById("telefonoEditar").value.trim(),
            fechaNacimiento: document.getElementById("fechaNacimientoEditar").value,
            historial: document.getElementById("historialEditar").value.trim(),
            activo: document.getElementById("activoEditar").value == "true"
        })
    })
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status} No se ha podido editar el paciente.`; });
            dialogEditarPaciente.close();
            cargarPacientes();
        })
        .catch((error) =>
            alert(error)
        );
}

// MODIFICAR ESTADO PACIENTE (Interruptor)
// Se hace un fetch (metodo PUT) al metodo que acciona el interruptor
// En el backend, el paciente cambia al estado contrario (activo -> inactivo, inactivo -> activo)
function cambiarEstado(id) {
    fetch("/medico/pacientes/" + id + "/estado", { method: "PUT" })
        .then(cargarPacientes)
        .catch(() => mostrarError("Error al modificar estado"));
}



/*

    LOS MÉDICOS NO TIENEN PERMITIDO ELIMINAR PACIENTES

// ELIMINAR PACIENTE
function eliminarPaciente(id) {
    // Se pide confirmación
    if (!confirm(`¿Estás seguro/a de que quieres eliminar el paciente con id ${id}?`)) return;

    // Se hace un fetch con metodo DELETE para eliminar el paciente de la base de datos
    fetch("/medico/pacientes/" + id, { method: "DELETE" })
        .then(cargarPacientes) // Se recarga la tabla
        .catch(() => mostrarError("Error al eliminar paciente."));
}

*/