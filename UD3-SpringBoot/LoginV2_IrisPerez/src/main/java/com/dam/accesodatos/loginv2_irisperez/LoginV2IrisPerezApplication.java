package com.dam.accesodatos.loginv2_irisperez;

import com.dam.accesodatos.loginv2_irisperez.controller.UsuarioController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoginV2IrisPerezApplication implements CommandLineRunner {

    @Autowired
    private UsuarioController usuarioController;

    public static void main(String[] args) {
        SpringApplication.run(LoginV2IrisPerezApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        usuarioController.iniciarSesion();
    }
}
