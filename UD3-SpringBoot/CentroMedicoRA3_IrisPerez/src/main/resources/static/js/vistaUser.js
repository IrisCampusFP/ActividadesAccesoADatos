let spanNombreUsuario = document.getElementById("nombreUsuario")
let divDatosUsuario = document.getElementById("datosUsuario")

// Se obtienen los datos del usuario llamando a la API
fetch("/user/datos", {
    method: "GET",
    headers: {
        "Content-Type": "application/json"
    },
}).then((response) => {
    if(response.ok){
        return response.json();
    } else {
        throw new Error(`Error ${response.status}: ${response.statusText}`);
    }
}).then((data) => {
    // Se muestra el nombre de usuario en el saludo y en el título
    spanNombreUsuario.textContent = `${data.username}`;
    document.title = "Página de bienvenida (" + data.username + ")";

    // Se crea una tabla con los datos recibidos
    let tablaHtml = `
        <div class="table-responsive">
            <table class="table table-dark table-hover align-middle">
                <tbody>
                    <tr>
                        <td><strong>Nombre</strong></td>
                        <td>${data.nombre}</td>
                    </tr>
                    <tr>
                        <td><strong>Apellidos</strong></td>
                        <td>${data.apellidos}</td>
                    </tr>
                    <tr>
                        <td><strong>Username</strong></td>
                        <td>${data.username}</td>
                    </tr>
                    <tr>
                        <td><strong>Email</strong></td>
                        <td>${data.email}</td>
                    </tr>
                    <tr>
                        <td><strong>DNI</strong></td>
                        <td>${data.dni}</td>
                    </tr>
                    <tr>
                        <td><strong>Estado</strong></td>
                        <td>${data.activo ? 'Activo' : 'Inactivo'}</td>
                    </tr>
                    <tr>
                        <td><strong>Fecha de creación</strong></td>
                        <td>${mostrarFecha(data.fechaCreacion)}</td>
                    </tr>
                    <tr>
                        <td><strong>Fecha última actualización</strong></td>
                        <td>${mostrarFecha(data.fechaActualizacion)}</td>
                    </tr>
                    <tr>
                        <td><strong>Último login</strong></td>
                        <td>${mostrarFecha(data.ultimoLogin)}</td>
                    </tr>
                    <tr>
                        <td><strong>Rol / Roles</strong></td>
                        <td>${mostrarRoles(data.roles)}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    `;

    // Se inyecta la tabla en el div con id 'datosUsuario'
    divDatosUsuario.innerHTML = tablaHtml;
}).catch((error) => {
    console.error("Fallo en login:", error);
});

function mostrarFecha(fecha) {
    return fecha ? new Date(fecha).toLocaleString() : "-";
}

function mostrarRoles(roles) {
    if (!roles || roles.size === 0) return "-";
    return Array.from(roles).map(rol => rol.nombre).join(', ');
}


/* APUNTES:
Diferencia entre 'innerHtml' y 'textContent'
-innerHtml: incluye etiquetas html como <div>, <span>, <a>
-textContent: se utiliza cuando solo quieres enviar texto plano, ignora las etiquetas html (no las interpreta)
* */