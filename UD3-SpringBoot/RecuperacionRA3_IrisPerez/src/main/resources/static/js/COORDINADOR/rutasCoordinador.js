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
    fetch("/coordinador/rutas")
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


