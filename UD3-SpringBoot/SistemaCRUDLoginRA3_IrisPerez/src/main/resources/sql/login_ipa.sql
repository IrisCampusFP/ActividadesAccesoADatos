DROP DATABASE IF EXISTS login_ipa;
CREATE DATABASE IF NOT EXISTS login_ipa
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE login_ipa;

-- Tabla usuario
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    dni VARCHAR(9) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Agregado el campo de fecha_actualizacion
    ultimo_login TIMESTAMP DEFAULT NULL,
    intentos_fallidos INT DEFAULT 0
);

-- Tabla rol
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de asociación rol_usuario
CREATE TABLE rol_usuario (
    id_usuario BIGINT NOT NULL,
    id_rol BIGINT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_rol_usuario_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_rol_usuario_rol FOREIGN KEY (id_rol)
        REFERENCES roles(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Usuarios de ejemplo (password = 1234 codificada con BCrypt)
INSERT INTO usuarios (nombre, apellidos, username, email, password, dni, activo, fecha_creacion, fecha_actualizacion, ultimo_login) VALUES
    ('Iris', 'Pérez Aparicio', 'irisperezz', 'iris@gmail.com',
     '$2a$10$WHUWMirDJuVxCokGxvVnoOal6ffgY4sjJ4EXuJtFtakLj/ygutbBe',
     '12345678A', TRUE, NOW(), NOW(), NULL),

    ('Carlos', 'López Martín', 'carlosl', 'carlos@gmail.com',
     '$2a$10$WHUWMirDJuVxCokGxvVnoOal6ffgY4sjJ4EXuJtFtakLj/ygutbBe',
     '23456789B', TRUE, NOW(), NOW(), NULL),

    ('Laura', 'Sánchez Ruiz', 'lauras', 'laura@gmail.com',
     '$2a$10$WHUWMirDJuVxCokGxvVnoOal6ffgY4sjJ4EXuJtFtakLj/ygutbBe',
     '34567890C', TRUE, NOW(), NOW(), NULL),

    ('Mario', 'Fernández Torres', 'mariof', 'mario@gmail.com',
     '$2a$10$WHUWMirDJuVxCokGxvVnoOal6ffgY4sjJ4EXuJtFtakLj/ygutbBe',
     '45678901D', TRUE, NOW(), NOW(), NULL),

    ('Ana', 'Moreno Castillo', 'anam', 'ana@gmail.com',
     '$2a$10$WHUWMirDJuVxCokGxvVnoOal6ffgY4sjJ4EXuJtFtakLj/ygutbBe',
     '56789012E', TRUE, NOW(), NOW(), NULL);

-- Roles
INSERT INTO roles (id, nombre) VALUES ('1', 'admin'), ('2', 'user');

-- Asignación de usuarios a roles
INSERT INTO rol_usuario (id_usuario, id_rol) VALUES
    (1, 1), -- Iris → admin
    (2, 2), -- Carlos → user
    (3, 2), -- Laura → user
    (4, 2), -- Mario → user
    (5, 2); -- Ana → user
