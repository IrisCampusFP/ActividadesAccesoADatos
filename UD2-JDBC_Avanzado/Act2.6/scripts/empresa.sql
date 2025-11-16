CREATE DATABASE IF NOT EXISTS empresa;
USE empresa;

CREATE TABLE empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50),
    salario DOUBLE
);

INSERT INTO empleados (nombre, salario) VALUES ('Ana', 24000), ('Luis', 28000);

-- Procedimiento para obtener los datos de un empleado por su id
DELIMITER //
CREATE PROCEDURE obtener_empleado(IN empId INT)
BEGIN
SELECT id, nombre, salario FROM empleados WHERE id = empId;
END //
DELIMITER ;

-- Procedimiento para incrementar el salario de un empleado
DELIMITER //

CREATE PROCEDURE incrementar_salario(IN empId INT,IN incremento DOUBLE,OUT nuevoSalario DOUBLE)
BEGIN
UPDATE empleados SET salario = salario + incremento WHERE id = empId;

SELECT salario INTO nuevoSalario FROM empleados WHERE id = empId;
END //

DELIMITER ;
