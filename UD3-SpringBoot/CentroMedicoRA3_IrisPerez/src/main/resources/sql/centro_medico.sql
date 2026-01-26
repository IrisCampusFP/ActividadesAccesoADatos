DROP DATABASE IF EXISTS centro_medico_ipa;
CREATE DATABASE IF NOT EXISTS centro_medico_ipa
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE centro_medico_ipa;

-- Tabla usuarios
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla roles
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de asociación rol_usuario
CREATE TABLE rol_usuario (
    id_usuario BIGINT AUTO_INCREMENT NOT NULL,
    id_rol BIGINT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_rol_usuario_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_rol_usuario_rol FOREIGN KEY (id_rol)
        REFERENCES roles(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Tabla pacientes
CREATE TABLE pacientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(15) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    fecha_nacimiento DATE,
    historial TEXT,
    medico_id BIGINT REFERENCES usuarios(id),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_medico_paciente FOREIGN KEY (medico_id)
        REFERENCES usuarios(id) ON DELETE CASCADE ON UPDATE CASCADE
);


-- INSERCIÓN DE DATOS DE PRUEBA

-- Usuarios de ejemplo
INSERT INTO usuarios (username, email, password_hash, nombre, activo, fecha_creacion) VALUES
    ('irisperezz', 'iris@gmail.com', '$2a$10$WHUWMirDJuVxCokGxvVnoOal6ffgY4sjJ4EXuJtFtakLj/ygutbBe', 'Iris Perez',
     TRUE,NOW()),

    ('carlosloopez1', 'carlos@gmail.com', '$2a$10$WHUWMirDJuVxCokGxvVnoOal6ffgY4sjJ4EXuJtFtakLj/ygutbBe', 'Carlos Lopez', TRUE, NOW()),

    ('lauraa123', 'laura@gmail.com', '$2a$10$WHUWMirDJuVxCokGxvVnoOal6ffgY4sjJ4EXuJtFtakLj/ygutbBe', 'Laura Sanchez',
     TRUE, NOW()),

    ('mario321', 'mario@gmail.com', '$2a$10$WHUWMirDJuVxCokGxvVnoOal6ffgY4sjJ4EXuJtFtakLj/ygutbBe', 'Mario Ruiz',
     TRUE, NOW());

-- Roles
INSERT INTO roles (id, nombre) VALUES ('1', 'ADMIN'), ('2', 'MEDICO'), ('3', 'RECEPCION');

-- Asignación de usuarios a roles
INSERT INTO rol_usuario (id_usuario, id_rol) VALUES
    (1, 1), -- Iris → ADMIN
    (2, 2), -- Carlos → MEDICO
    (3, 2), -- Laura → MEDICO
    (4, 3); -- Mario → RECEPCION

-- Pacientes de ejemplo
INSERT INTO pacientes (nombre, apellidos, dni, telefono, fecha_nacimiento, historial, medico_id, activo, fecha_creacion) VALUES
    ('Paco', 'Martin Sanchez', '22233344A', '654321456', '2005-10-07',
     'texto ejemplo historial medico', 2, TRUE, NOW()),

    ('Ana', 'Garcia Saez', '11155522B', '614543768', '2000-7-11',
     'texto ejemplo historial medico', 2, TRUE, NOW()),

    ('Pepe', 'Lopez Aparicio', '22244411A', '694976453', '1985-09-02',
     'texto ejemplo historial medico', 3, TRUE, NOW()),

    ('Juan', 'Arroyo Martin', '77755544A', '634234634', '1976-06-12',
     'texto ejemplo historial medico', 3, TRUE, NOW());
