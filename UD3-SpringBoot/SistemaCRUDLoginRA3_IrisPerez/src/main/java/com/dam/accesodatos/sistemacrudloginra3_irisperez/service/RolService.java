package com.dam.accesodatos.sistemacrudloginra3_irisperez.service;

import com.dam.accesodatos.sistemacrudloginra3_irisperez.entity.Rol;
import com.dam.accesodatos.sistemacrudloginra3_irisperez.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RolService {

    @Autowired
    RolRepository rolRepository;

    // Obtener todos los roles
    @Transactional(readOnly = true)
    public List<Rol> obtenerRoles() {
        return rolRepository.findAll();
    }
}
