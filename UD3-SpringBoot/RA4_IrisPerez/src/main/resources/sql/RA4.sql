-- 1. ELIMINAR TABLAS (y sus datos, índices y triggers)
-- Usamos CASCADE para asegurar que se borren las herencias y claves foráneas

-- Eliminar tablas de la jerarquía de herencia (Hijas primero o Padre con CASCADE)
DROP TABLE IF EXISTS libros CASCADE;
DROP TABLE IF EXISTS revistas CASCADE;
DROP TABLE IF EXISTS recursos CASCADE;

-- Eliminar tablas independientes
DROP TABLE IF EXISTS usuarios CASCADE;
DROP TABLE IF EXISTS productos CASCADE;

-- 2. ELIMINAR SECUENCIAS
-- (Si se crearon independientemente y no como SERIAL puro atado a columna)
DROP SEQUENCE IF EXISTS recursos_id_seq;

-- 3. ELIMINAR FUNCIONES
-- Es importante borrar la función antes que el tipo que usa como parámetro
DROP FUNCTION IF EXISTS producto_disponible(tipo_producto);

-- 4. ELIMINAR TIPOS COMPUESTOS
-- Usamos CASCADE por si quedó alguna tabla o función huérfana dependiendo de ellos
DROP TYPE IF EXISTS tipo_producto CASCADE;
DROP TYPE IF EXISTS tipo_direccion CASCADE;
DROP TYPE IF EXISTS tipo_telefono CASCADE;
-- Crear tipo compuesto
CREATE TYPE tipo_producto AS (
    codigo      INTEGER,
    nombre      VARCHAR(100),
    precio      NUMERIC(10,2),
    stock       INTEGER,
    activo      BOOLEAN
);

-- Función equivalente al método
CREATE OR REPLACE FUNCTION producto_disponible(p tipo_producto)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN p.stock > 0 AND p.activo = true;
END;
$$ LANGUAGE plpgsql;

-- Tabla con columna de tipo compuesto
CREATE TABLE IF NOT EXISTS productos (
    id SERIAL PRIMARY KEY,
    datos tipo_producto NOT NULL
);

-- Restricción de unicidad en código (expresión sobre tipo compuesto)
-- Ojo: hay que encerrar toda la expresión en paréntesis extra
CREATE UNIQUE INDEX IF NOT EXISTS idx_producto_codigo ON productos (((datos).codigo));

-- 1) INSERT
INSERT INTO productos (datos) VALUES (
    ROW(1, 'Laptop', 999.99, 10, true)::tipo_producto
);

-- 2) SELECT con función
SELECT (datos).codigo, (datos).nombre, (datos).precio
FROM productos
WHERE producto_disponible(datos) = true;

-- 3) UPDATE (reconstruyendo el compuesto completo)
UPDATE productos
SET datos = ROW(
    (datos).codigo,
    (datos).nombre,
    1099.99,          -- nuevo precio
    (datos).stock,
    (datos).activo
)::tipo_producto
WHERE (datos).codigo = 1;

-- 4) DELETE
DELETE FROM productos
WHERE (datos).stock = 0;

-- Tipo dirección
CREATE TYPE tipo_direccion AS (
    calle   VARCHAR(200),
    ciudad  VARCHAR(100),
    cp      VARCHAR(10),
    pais    VARCHAR(50)
);

-- Tipo teléfono
CREATE TYPE tipo_telefono AS (
    tipo    VARCHAR(20),
    numero  VARCHAR(20)
);

-- Tabla usuarios con tipo anidado y array
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    direccion tipo_direccion,
    telefonos tipo_telefono[]  -- Array de tipo compuesto
);

-- Ejemplos de uso
-- Insertar usuario con dirección y 2 teléfonos
INSERT INTO usuarios (nombre, email, direccion, telefonos)
VALUES (
    'Ana Pérez',
    'ana@example.com',
    ROW('Av. Siempre Viva 123', 'Springfield', '12345', 'USA')::tipo_direccion,
    ARRAY[
        ROW('MÓVIL','600123123')::tipo_telefono,
        ROW('FIJO','911223344')::tipo_telefono
    ]
);

-- Consultar campos individuales de la dirección
SELECT
    (direccion).calle AS calle,
    (direccion).ciudad AS ciudad,
    (direccion).cp AS cp,
    (direccion).pais AS pais
FROM usuarios;

-- Filtrar por teléfonos que contengan un número
SELECT id, nombre, email
FROM usuarios
WHERE EXISTS (
    SELECT 1
    FROM unnest(telefonos) t
    WHERE (t).numero = '600123123'
);


-- Usamos una única secuencia para IDs en toda la jerarquía
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'recursos_id_seq') THEN
    CREATE SEQUENCE recursos_id_seq;
  END IF;
END $$;

-- Tabla base
CREATE TABLE IF NOT EXISTS recursos (
    id INTEGER PRIMARY KEY DEFAULT nextval('recursos_id_seq'),
    titulo VARCHAR(200) NOT NULL,
    anio INTEGER,
    disponible BOOLEAN DEFAULT true
);

-- Tabla derivada Libros
CREATE TABLE IF NOT EXISTS libros (
    isbn VARCHAR(20),
    autor VARCHAR(100),
    paginas INTEGER
) INHERITS (recursos);

-- Asegurar que usa la misma secuencia por defecto
ALTER TABLE libros ALTER COLUMN id SET DEFAULT nextval('recursos_id_seq');

-- Tabla derivada Revistas
CREATE TABLE IF NOT EXISTS revistas (
    issn VARCHAR(20),
    numero INTEGER,
    mes VARCHAR(20)
) INHERITS (recursos);

-- Asegurar que usa la misma secuencia por defecto
ALTER TABLE revistas ALTER COLUMN id SET DEFAULT nextval('recursos_id_seq');

-- Índices para las tablas hijas (unicidad de id por tabla)
CREATE UNIQUE INDEX IF NOT EXISTS libros_pkey ON libros (id);
CREATE UNIQUE INDEX IF NOT EXISTS revistas_pkey ON revistas (id);

-- Ejemplos de inserción
INSERT INTO libros (titulo, anio, disponible, isbn, autor, paginas)
VALUES ('El Quijote', 1605, true, 'ISBN-123', 'Miguel de Cervantes', 863);

INSERT INTO revistas (titulo, anio, disponible, issn, numero, mes)
VALUES ('Ciencia Hoy', 2025, true, 'ISSN-987', 12, 'Enero');

-- Consultar toda la jerarquía (padre incluye filas de hijas)
SELECT id, titulo, anio, disponible FROM recursos ORDER BY id;

-- Consultar sólo la tabla base (excluyendo hijas)
SELECT id, titulo FROM ONLY recursos;

-- Consultar sólo libros
SELECT id, titulo, autor, paginas FROM libros;

-- Consultar sólo revistas
SELECT id, titulo, numero, mes FROM revistas;


-- 1. Nombre y ciudad
SELECT nombre, (direccion).ciudad
FROM usuarios;

-- 2. Usuarios de Madrid
SELECT id, nombre, email
FROM usuarios
WHERE (direccion).ciudad = 'Madrid';

-- 3. Usuarios con teléfonos desanidados (recomendado: LATERAL + COALESCE)
SELECT u.nombre,
       (t).tipo   AS tipo_telefono,
       (t).numero AS numero_telefono
FROM usuarios u
CROSS JOIN LATERAL unnest(COALESCE(u.telefonos, ARRAY[]::tipo_telefono[])) AS t;

-- 3b. Sintaxis breve (funciona, pero prefiero la anterior por claridad)
-- SELECT u.nombre, (t).tipo AS tipo_telefono, (t).numero
-- FROM usuarios u, unnest(u.telefonos) AS t;

-- 4. Alternativa para arrays simples de strings
-- (si 'telefonos' fuera text[] en vez de tipo_telefono[])
SELECT nombre, unnest(telefonos) AS telefono
FROM usuarios;

-- 1. Consulta polimórfica (incluye libros y revistas)
SELECT id, titulo, anio, disponible
FROM recursos
ORDER BY id;

-- 2. Solo tabla padre (ONLY)
SELECT id, titulo, anio
FROM ONLY recursos
ORDER BY id;

-- 3. Identificar tipo de fila por tabla origen
SELECT id, titulo,
       CASE tableoid::regclass::text
           WHEN 'libros'   THEN 'LIBRO'
           WHEN 'revistas' THEN 'REVISTA'
           WHEN 'recursos' THEN 'RECURSO_BASE'
           ELSE 'DESCONOCIDO'
       END AS tipo
FROM recursos
ORDER BY id;




-- 1) Cambiar la ciudad (y CP) de un usuario: reconstruyendo el tipo compuesto
-- Nota: No se puede usar "SET direccion.ciudad = ...".
UPDATE usuarios
SET direccion = ROW(
    (direccion).calle,
    'Barcelona',
    '08001',
    (direccion).pais
)::tipo_direccion
WHERE id = 1;

-- Alternativa: reemplazar toda la dirección directamente
UPDATE usuarios
SET direccion = ROW('Nueva Calle', 'Barcelona', '08001', 'España')::tipo_direccion
WHERE id = 1;

-- 2) Agregar un teléfono a un array existente (tipo compuesto)
-- Usamos COALESCE para evitar problemas si telefonos es NULL.
UPDATE usuarios
SET telefonos = array_append(
    COALESCE(telefonos, ARRAY[]::tipo_telefono[]),
    ROW('nuevo', '600123456')::tipo_telefono
)
WHERE id = 1;

-- 2b) Para arrays simples de strings (text[])
-- UPDATE usuarios
-- SET telefonos = array_append(COALESCE(telefonos, ARRAY[]::text[]), '600123456')
-- WHERE id = 1;

-- 3) Eliminar un teléfono específico del array
-- Para arrays de tipo compuesto: hay que pasar el valor compuesto tipado.
UPDATE usuarios
SET telefonos = array_remove(
    telefonos,
    ROW('nuevo', '600123456')::tipo_telefono
)
WHERE id = 1;

-- 3b) Si fuera un array simple de strings:
-- UPDATE usuarios
-- SET telefonos = array_remove(telefonos, '666111222')
-- WHERE id = 1;


-- 1) Usuarios sin teléfonos (NULL o array vacío)
DELETE FROM usuarios
WHERE telefonos IS NULL
   OR array_length(telefonos, 1) = 0;

-- 2) Libros con menos de 100 páginas
DELETE FROM libros
WHERE paginas < 100;







-- 1. Actualizar campo de tipo compuesto
UPDATE usuarios
SET direccion.ciudad = 'Barcelona',
    direccion.cp = '08001'
WHERE id = 1;

-- Alternativa: reemplazar todo el tipo
UPDATE usuarios
SET direccion = ROW('Nueva Calle', 'Barcelona', '08001', 'España')::tipo_direccion
WHERE id = 1;

-- 2. Agregar teléfono al array
UPDATE usuarios
SET telefonos = array_append(
    telefonos, 
    ROW('nuevo', '600123456')::tipo_telefono
)
WHERE id = 1;

-- Para arrays simples de strings:
UPDATE usuarios
SET telefonos = array_append(telefonos, '600123456')
WHERE id = 1;

-- 3. Eliminar teléfono del array (por valor)
UPDATE usuarios
SET telefonos = array_remove(telefonos, '666111222')
WHERE id = 1;





-- 1. Usuarios sin teléfonos
DELETE FROM usuarios
WHERE telefonos IS NULL OR array_length(telefonos, 1) = 0;

-- 2. Libros con menos de 100 páginas
DELETE FROM libros
WHERE paginas < 100;





DO $$
DECLARE
    v_stock INTEGER;
BEGIN
    -- 1. Insertar nuevo producto
    INSERT INTO productos (datos) VALUES (
        ROW(100, 'Nuevo Producto', 199.99, 50, true)::tipo_producto
    );
    
    -- 2. Actualizar stock
    UPDATE productos
    SET datos.stock = (datos).stock - 5
    WHERE (datos).codigo = 1;
    
    -- Verificar stock
    SELECT (datos).stock INTO v_stock
    FROM productos
    WHERE (datos).codigo = 1;
    
    IF v_stock < 0 THEN
        RAISE EXCEPTION 'Stock insuficiente';
    END IF;
    
    -- Commit implícito al terminar el bloque sin error
    RAISE NOTICE 'Transacción completada';
    
EXCEPTION
    WHEN OTHERS THEN
        -- Rollback automático en caso de excepción
        RAISE NOTICE 'Error: %', SQLERRM;
        RAISE;
END;
$$;





BEGIN;

-- Producto 1
INSERT INTO productos (datos) VALUES (
    ROW(201, 'Producto A', 100.00, 10, true)::tipo_producto
);
SAVEPOINT sp_producto_a;

-- Producto 2
INSERT INTO productos (datos) VALUES (
    ROW(202, 'Producto B', 200.00, 20, true)::tipo_producto
);
SAVEPOINT sp_producto_b;

-- Producto 3 (si falla, rollback parcial)
-- Simulación: intentar algo que falle
-- ROLLBACK TO SAVEPOINT sp_producto_b;

COMMIT;






-- Limpieza previa
DROP TYPE IF EXISTS tipo_usuario CASCADE;
DROP TYPE IF EXISTS tipo_libro CASCADE;   -- En Postgres no hay herencia de tipos,
DROP TYPE IF EXISTS tipo_revista CASCADE; -- así que definimos tipos independientes
DROP TYPE IF EXISTS tipo_recurso CASCADE; -- o usamos herencia de tablas después.
DROP TYPE IF EXISTS tipo_telefono CASCADE;
DROP TYPE IF EXISTS tipo_direccion CASCADE;

-- =====================================================
-- 1. TIPOS BÁSICOS
-- =====================================================

-- Cambio: AS (...) en lugar de AS OBJECT (...)
-- Cambio: VARCHAR en lugar de VARCHAR2
CREATE TYPE tipo_direccion AS (
    calle           VARCHAR(200),
    ciudad          VARCHAR(100),
    codigo_postal   VARCHAR(10),
    pais            VARCHAR(50)
);

CREATE TYPE tipo_telefono AS (
    tipo    VARCHAR(20),
    numero  VARCHAR(20)
);

-- =====================================================
-- 2. COLECCIONES
-- =====================================================
-- NOTA: En Postgres NO se crea un tipo para la lista.
-- Se usa directamente tipo_telefono[] dentro de las tablas u otros tipos.

-- =====================================================
-- 3. TIPOS COMPUESTOS (Anidados)
-- =====================================================

CREATE TYPE tipo_usuario AS (
    id          INTEGER, -- NUMBER no existe, usamos INTEGER o NUMERIC
    nombre      VARCHAR(100),
    email       VARCHAR(100),
    direccion   tipo_direccion,
    -- AQUÍ está la magia en Postgres: Array nativo del tipo compuesto
    telefonos   tipo_telefono[] 
);

-- =====================================================
-- 4. JERARQUÍA DE RECURSOS
-- =====================================================
-- IMPORTANTE: Postgres NO soporta 'CREATE TYPE ... UNDER ...'.
-- Tienes dos estrategias aquí:

-- ESTRATEGIA A: Definir solo el tipo base y usar HERENCIA DE TABLAS (Recomendado)
CREATE TYPE tipo_recurso_datos AS (
    titulo      VARCHAR(200),
    anio        INTEGER,
    disponible  BOOLEAN  -- Postgres tiene BOOLEAN nativo, mejor que CHAR(1)
);

-- Si necesitas tipos específicos para pasar como parámetros en funciones,
-- tendrías que crearlos por separado sin relación de herencia:

CREATE TYPE tipo_libro_datos AS (
    titulo      VARCHAR(200),
    anio        INTEGER,
    disponible  BOOLEAN,
    isbn        VARCHAR(20),
    autor       VARCHAR(100),
    paginas     INTEGER
);

-- (La herencia real la harás en el script 02 al crear las TABLAS)
