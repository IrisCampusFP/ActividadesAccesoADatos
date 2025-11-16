-- 1. CREACIÓN DE LA BASE DE DATOS
CREATE DATABASE IF NOT EXISTS TechDAM_IrisPerez;
USE TechDAM_IrisPerez;

-- 2. CREACIÓN DE LAS TABLAS

-- Creación de la tabla empleados
CREATE TABLE empleados (
    id_empleado INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    departamento VARCHAR(50),
    salario DECIMAL(10, 2),
    activo BOOLEAN DEFAULT TRUE
);

-- Creación de la tabla proyectos
CREATE TABLE proyectos (
    id_proyecto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    presupuesto DECIMAL(15, 2),
    fecha_inicio DATE,
    fecha_fin DATE
);

-- Creación de la tabla asignaciones
CREATE TABLE asignaciones (
    id_asignacion INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado INT,
    id_proyecto INT,
    fecha DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado) ON DELETE CASCADE,
    FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto) ON DELETE CASCADE,
    CONSTRAINT restriccionAsignacionesDuplicadas UNIQUE (id_empleado, id_proyecto)
);


-- 3. INSERCIÓN DE DATOS DE PRUEBA

-- Registros de prueba tabla empleados
INSERT INTO empleados (nombre, departamento, salario, activo) VALUES
    ('Ana Pérez', 'Desarrollo', 2200.00, TRUE),
    ('Luis Gómez', 'Marketing', 1800.00, TRUE),
    ('Marta Díaz', 'Desarrollo', 2500.00, TRUE),
    ('Carlos López', 'Recursos Humanos', 1700.00, TRUE),
    ('Laura Fernández', 'Ventas', 2000.00, FALSE),
    ('Sergio Martín', 'Desarrollo', 2400.00, TRUE),
    ('Patricia Ruiz', 'Marketing', 1900.00, TRUE),
    ('Jorge Sánchez', 'Ventas', 2100.00, TRUE),
    ('Elena Torres', 'Recursos Humanos', 1750.00, TRUE),
    ('Marcos Vidal', 'Desarrollo', 2600.00, TRUE),
    ('Isabel Romero', 'Marketing', 1850.00, FALSE),
    ('Rubén Navarro', 'Ventas', 1950.00, TRUE),
    ('Claudia Herrera', 'Desarrollo', 2300.00, TRUE),
    ('Diego Álvarez', 'Recursos Humanos', 1800.00, TRUE),
    ('Cristina Santos', 'Marketing', 2000.00, TRUE);

-- Registros de prueba tabla proyectos
INSERT INTO proyectos (nombre, presupuesto, fecha_inicio, fecha_fin) VALUES
    ('Desarrollo App Móvil', 50000.00, '2025-01-01', '2025-12-31'),
    ('Campaña Marketing 2025', 30000.00, '2025-03-01', '2025-06-30'),
    ('Expansión Internacional', 150000.00, '2025-05-01', '2025-12-31'),
    ('Automatización de Procesos', 100000.00, '2025-04-15', '2025-11-30'),
    ('Desarrollo Plataforma E-commerce', 75000.00, '2025-02-01', '2025-09-30'),
    ('Sistema de Gestión Empresarial', 120000.00, '2025-06-01', '2026-05-31'),
    ('Reestructuración de Marca', 45000.00, '2025-07-10', '2025-10-20'),
    ('Portal de Atención al Cliente', 65000.00, '2025-03-15', '2025-09-15'),
    ('Análisis de Datos Corporativos', 85000.00, '2025-02-20', '2025-11-30'),
    ('Optimización del CRM', 40000.00, '2025-04-01', '2025-08-31'),
    ('Campaña Internacional LATAM', 90000.00, '2025-05-05', '2025-12-20'),
    ('Integración ERP', 150000.00, '2025-01-20', '2026-01-20'),
    ('Proyecto de Cultura Empresarial', 30000.00, '2025-09-01', '2026-02-28'),
    ('Sistema de Logística Inteligente', 110000.00, '2025-03-25', '2025-12-15'),
    ('Migración a Infraestructura Cloud', 140000.00, '2025-02-05', '2026-01-10');

-- Registros de prueba tabla asignaciones
INSERT INTO asignaciones (id_empleado, id_proyecto, fecha) VALUES
    (1, 1, '2025-01-10'),
    (2, 2, '2025-03-05'),
    (3, 3, '2025-05-10'),
    (4, 4, '2025-04-20'),
    (1, 5, '2025-02-10'),
    (6, 6, '2025-06-05'),
    (7, 7, '2025-07-15'),
    (8, 6,'2025-06-10'),
    (9, 8, '2025-09-10'),
    (10, 6, '2025-06-12'),
    (11, 9, '2025-03-20'),
    (12, 10, '2025-07-20'),
    (13, 11, '2025-03-18'),
    (14, 12, '2025-02-25'),
    (15, 13, '2025-04-03'),
    (6, 14, '2025-05-10'),
    (11, 15, '2025-02-22'),
    (13, 3, '2025-05-08'),
    (15, 2, '2025-03-10'),
    (8, 9, '2025-09-05'),
    (12, 11, '2025-05-12'),
    (14, 14, '2025-09-07'),
    (10, 15, '2025-02-10'),
    (9, 2, '2025-01-28'),
    (7, 8, '2025-03-27');


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
