USE empresa;

CREATE TABLE cuentas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titular VARCHAR(100),
    saldo DECIMAL(10,2)
);

INSERT INTO cuentas (titular, saldo) VALUES
    ('Ana', 2000.00),
    ('Luis', 1500.00);