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
        if(response.ok){
            return response.json();
        } else {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
    }).then(() => {
        window.location.href = "/vista"
    }).catch((error) => {
        // En caso de error:
        console.error("Fallo en login:", error);
        alert("No se ha podido iniciar sesión. Email o contraseña incorrectos.")
    });
})