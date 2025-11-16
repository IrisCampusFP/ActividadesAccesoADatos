-- Creación de la base de datos
CREATE DATABASE IF NOT EXISTS empresa;
USE empresa;

-- Tabla EMPLEADOS
CREATE TABLE empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    salario DECIMAL(10,2)
);

-- Tabla PROYECTOS
CREATE TABLE proyectos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    presupuesto DECIMAL(10,2)
);

-- Tabla ASIGNACIONES (relación N:M entre empleados y proyectos)
CREATE TABLE IF NOT EXISTS asignaciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado INT NOT NULL,
    id_proyecto INT NOT NULL,
    rol VARCHAR(100),
    fecha_asignacion DATE NOT NULL,
    CONSTRAINT fk_asig_empleado FOREIGN KEY (id_empleado) REFERENCES empleados(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_asig_proyecto FOREIGN KEY (id_proyecto) REFERENCES proyectos(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT restriccionEvitarDuplicados UNIQUE (id_empleado, id_proyecto)
    );
-- (La última línea es una restricción de tipo UNIQUE que impide que un empleado esté asignado dos veces al mismo proyecto)


-- Procedimiento almacenado para asignar empleados a proyectos
DELIMITER $$
CREATE PROCEDURE asignar_empleado_a_proyecto (
    IN idEmpleadoEntrada INT,
    IN idProyectoEntrada INT,
    IN rolEntrada VARCHAR(100)
)
BEGIN
    DECLARE existeEmpleado INT;
    DECLARE existeProyecto INT;
	DECLARE existeAsignacion INT;

    -- Verificación de que el empleado existe
SELECT COUNT(*) INTO existeEmpleado FROM empleados WHERE id = idEmpleadoEntrada;
-- (si no existe el empleado lanza error)
IF existeEmpleado = 0 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El empleado no existe';
END IF;

    -- Verificación de que el proyecto existe
SELECT COUNT(*) INTO existeProyecto FROM proyectos WHERE id = idProyectoEntrada;
-- (si no existe el proyecto lanza error)
IF existeProyecto = 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El proyecto no existe'; END IF;

-- Antes de crear la asignación, se comprueba que no exista ya en la BDD
SELECT COUNT(*) INTO existeAsignacion FROM asignaciones WHERE id_empleado = idEmpleadoEntrada AND id_proyecto = idProyectoEntrada;
-- (si no existe la asignación lanza error)
IF existeAsignacion > 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El empleado ya está asignado a este proyecto';
ELSE
    INSERT INTO asignaciones (id_empleado, id_proyecto, rol, fecha_asignacion)
    VALUES (idEmpleadoEntrada, idProyectoEntrada, rolEntrada, CURRENT_DATE());
END IF;
END $$
DELIMITER ;