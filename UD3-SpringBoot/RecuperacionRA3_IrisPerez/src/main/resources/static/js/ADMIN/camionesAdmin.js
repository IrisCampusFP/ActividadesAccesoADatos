const tbodyCamiones = document.getElementById("tBodyCamiones");
const recuadroAlert = document.getElementById("recuadroAlert");

const dialogCrearCamion = document.getElementById("dialogCrearCamion");
const dialogEditarCamion = document.getElementById("dialogEditarCamion");

fetch("/user/datos")
    .then(r => r.json())
    .then(data => {
        document.title = "Camiones Registrados (" + data.nombre + ")";
    });

cargarCamiones();

function cargarCamiones() {
    tbodyCamiones.innerHTML =
        '<tr><td colspan="100%" class="text-center">Cargando camiones...</td></tr>';

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

            let tBody = "";
            camiones.forEach(c => {
                console.log(c);
                tBody += `
                    <tr>
                        <td>${c.id}</td>
                        <td>${c.matricula}</td>
                        <td>${c.modelo}</td>
                        <td>${c.capacidad}</td>
                        <td>${c.estado || "-"}</td>
                        <td>${c.fechaAlta ? new Date(c.fechaAlta).toLocaleDateString() : "-"}</td>
                        <td>${c.rutas && c.rutas.length > 0 ? c.rutas.length : "0"}</td>
                        <td> 
                            <div class="d-flex gap-2 justify-content-center"> 
                                <button class="btn btn-sm btn-outline-primary" 
                                    onclick="cargarDialogEditar(${c.id})">Editar</button> 
                                <button class="btn btn-sm btn-outline-warning" 
                                    onclick="cambiarEstado(${c.id})">${c.activo ? "Desactivar" : "Activar"}</button> 
                                <button class="btn btn-sm btn-outline-danger" 
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

document.getElementById("btnCrearCamion").onclick = () => {
    dialogCrearCamion.showModal();
};

const msgCrearError = document.getElementById("msgCrearError");

document.getElementById("formCrearCamion").onsubmit = e => {
    e.preventDefault();
    crearCamion();
};

function crearCamion() {
    fetch("/admin/camiones", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            matricula: document.getElementById("matriculaCrear").value.trim(),
            modelo: document.getElementById("modeloCrear").value.trim(),
            capacidad: parseInt(document.getElementById("capacidadCrear").value),
            estado: document.getElementById("estadoCrear").value,
            fechaAlta: document.getElementById("fechaAltaCrear").value || null,
            activo: document.getElementById("activoCrear").value === "true"
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
            document.getElementById("capacidadEditar").value = c.capacidad;
            document.getElementById("estadoEditar").value = c.estado || "DISPONIBLE";
            document.getElementById("fechaAltaEditar").value = c.fechaAlta || "";
            document.getElementById("activoEditar").value = c.activo;
            dialogEditarCamion.showModal();
        })
        .catch((error) =>
            alert(error)
        );
}

const editError = document.getElementById("editError");

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
            capacidad: parseInt(document.getElementById("capacidadEditar").value),
            estado: document.getElementById("estadoEditar").value,
            fechaAlta: document.getElementById("fechaAltaEditar").value || null,
            activo: document.getElementById("activoEditar").value === "true"
        })
    })
        .then(r => {
            if (!r.ok) return r.json().then(d => { throw d.errorMsg || `Error ${r.status} No se ha podido editar el camion.`; });
            dialogEditarCamion.close();
            cargarCamiones();
        })
        .catch((error) => {
            editError.textContent = error;
            editError.classList.remove("d-none");
        });
}

dialogEditarCamion.addEventListener("close", () => {
    editError.classList.add("d-none");
});

function cambiarEstado(id) {
    fetch("/admin/camiones/" + id + "/estado", { method: "PUT" })
        .then(cargarCamiones)
        .catch(() => mostrarError("Error al modificar estado"));
}

function eliminarCamion(id) {
    if (!confirm(`¿Estás seguro/a de que quieres eliminar el camion con id ${id}?`)) return;

    fetch("/admin/camiones/" + id, { method: "DELETE" })
        .then(cargarCamiones)
        .catch(() => mostrarError("Error al eliminar camion."));
}