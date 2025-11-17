DROP DATABASE BookWorld_IrisPerez;
-- 1. CREACIÓN DE LA BASE DE DATOS
CREATE DATABASE IF NOT EXISTS BookWorld_IrisPerez;
USE BookWorld_IrisPerez;

-- 2. CREACIÓN DE LAS TABLAS

-- Creación de la tabla de la entidad Genero
CREATE TABLE generos (
    id_genero INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL
);

-- Creación de la tabla de la entidad Libro
CREATE TABLE libros (
    id_libro INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL,
    id_genero INT,
	CONSTRAINT fk_id_genero FOREIGN KEY (id_genero) REFERENCES generos(id_genero) ON DELETE CASCADE
);

-- Creación de la tabla de la entidad Cliente
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    dni VARCHAR(100) NOT NULL,
	edad INT
);

-- Creación de la tabla de la entidad Pedido
CREATE TABLE ventas (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_libro INT,
    cantidad INT,
    CONSTRAINT fk_id_libro FOREIGN KEY (id_libro) REFERENCES libros(id_libro) ON DELETE CASCADE
);

-- Creación de la tabla de la entidad Venta
CREATE TABLE pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
	id_cliente INT,
    id_venta INT,
	CONSTRAINT fk_id_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE,
    CONSTRAINT fk_id_venta FOREIGN KEY (id_venta) REFERENCES ventas(id_venta) ON DELETE CASCADE
);



-- 3. INSERCIÓN DE DATOS DE PRUEBA

-- Registros de prueba tabla generos
INSERT INTO generos (descripcion) VALUES
    ('Ciencia ficción'),
    ('Comedia'),
    ('Drama'),
    ('Miedo');

-- Registros de prueba tabla libros
INSERT INTO libros (titulo, autor, precio, stock, id_genero) VALUES
    ('Libro ejemplo 1', 'Paco Perez', 22.99, 20, 1),
    ('Libro ejemplo 2', 'Ana García', 19, 25, 2),
    ('Libro ejemplo 3', 'Miguel Torres', 17.45, 30, 3),
    ('Libro ejemplo 4', 'Olivia Sanz', 6.99, 40, 4),
    ('Libro ejemplo 5', 'Francisco López', 12.60, 15, 2);
    
-- Registros de prueba tabla clientes
INSERT INTO clientes (nombre, apellidos, dni, edad) VALUES
    ('Luis', 'Gómez Sanz', '12345678C', 23),
    ('Carlos', 'Fernández López', '8765432A', 34),
    ('Laura', 'Fernández Rodriguez', '76543267K', 19),
    ('Sergio', 'Martín Navarro', '33345643H', 48),
    ('Patricia', 'Ruiz García', '84635756J', 56);

-- Registros de prueba tabla clientes
INSERT INTO clientes (nombre, apellidos, dni, edad) VALUES
    ('Luis', 'Gómez Sanz', '12345678C', 23),
    ('Carlos', 'Fernández López', '8765432A', 34),
    ('Laura', 'Fernández Rodriguez', '76543267K', 19),
    ('Sergio', 'Martín Navarro', '33345643H', 48),
    ('Patricia', 'Ruiz García', '84635756J', 56);

-- Registros de prueba tabla ventas
INSERT INTO ventas (id_libro, cantidad) VALUES
    (1, 3),
    (2, 1),
    (3, 2),
    (4, 2),
    (5, 3),
    (2, 2),
    (4, 1),
    (1, 2);
    
-- Registros de prueba tabla pedidos
INSERT INTO pedidos (id_cliente, id_venta) VALUES
    (1, 1),
    (1, 2),
    (2, 3),
    (2, 4),
    (3, 5),
    (4, 6),
    (4, 7),
    (5, 8);


-- 4. PROCEDIMIENTOS ALMACENADOS

-- A) Actualizar el salario de todos los empleados de un departamento

DELIMITER $$
CREATE PROCEDURE actualizar_salario_departamento(
    IN p_departamento VARCHAR(50),
    IN p_porcentaje DECIMAL(5,2),
    OUT p_empleados_actualizados INT
)
BEGIN
    UPDATE empleados
    SET salario = salario * (1 + p_porcentaje / 100) WHERE departamento = p_departamento AND activo = TRUE;
    SET p_empleados_actualizados = ROW_COUNT();
END$$
DELIMITER ;

-- B) Actualizar el estado de un empleado:
-- Si se actualiza un empleado a INACTIVO, se eliminan sus asignaciones

DELIMITER $$
CREATE PROCEDURE actualizar_estado_empleado(
    IN p_id_empleado INT,
    IN p_estado BOOLEAN
)
BEGIN
        -- Actualizar el estado de un empleado (activo/inactivo)
    UPDATE empleados
    SET activo = p_estado
    WHERE id_empleado = p_id_empleado;

    -- Si el empleado se marca como inactivo, se eliminan todas sus asignaciones
    IF p_estado = FALSE THEN
        DELETE FROM asignaciones WHERE id_empleado = p_id_empleado;
    END IF;
END$$
DELIMITER ;


-- 5. FUNCIÓN ALMACENADA
-- Calcular la suma de todos los salarios de un departamento

DELIMITER $$
CREATE FUNCTION suma_salarios_departamento(
    departamentoEntrada VARCHAR(50)
)
    RETURNS DECIMAL(15,2)
    DETERMINISTIC
BEGIN
    DECLARE suma_salarios DECIMAL(15,2);

        -- Cálculo del salario total de los empleados del departamento especificado
    SELECT SUM(salario) INTO suma_salarios
    FROM empleados
    WHERE departamento = departamentoEntrada AND activo = TRUE;

    -- Devuelve la suma de todos los salarios
    RETURN IFNULL(suma_salarios, 0);
END$$
DELIMITER ;
