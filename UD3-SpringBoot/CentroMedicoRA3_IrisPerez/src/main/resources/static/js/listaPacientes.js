var tbodyPacientes = document.getElementById("tBodyPacientes");
var recuadroAlert = document.getElementById("recuadroAlert");

// Mostrar nombre del usuario (recepcionista) que esté usando el panel en el title
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
    fetch("/recepcion/pacientes")
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status}: No se ha podido cargar la lista de pacientes.`; });
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
                        <td>${p.medico ? p.medico.nombre : "-"}</td>
                        <td>${p.activo ? '<span class="badge text-bg-success">Activo</span>' : '<span class="badge text-bg-danger">Inactivo</span>'}</td>
                        <td>${p.fechaCreacion ? new Date(p.fechaCreacion).toLocaleString() : "-"}</td>
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
