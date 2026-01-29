let formulario = document.getElementById("formulario")
let email = document.getElementById("email")
let password = document.getElementById("password")

fetch("/killSession")

formulario.addEventListener("submit", (e) => {
    e.preventDefault();

    var emailRecibido = email.value.trim()
    var passwordRecibida = password.value.trim();

    fetch("/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email: emailRecibido,
            password: passwordRecibida
        })
    }).then((response) => {
        if(!response.ok){
            // Intentar leer mensaje de error del backend
            return response.json().then((data) => {
                throw data.mensaje;
            })
        }
        return response.json();
    }).then(() => {
        window.location.href = "/vista"
    }).catch((error) => {
        // En caso de error:
        alert(error);
    });
})