# Proyecto TechDAM - JDBC Avanzado 

![Java](https://img.shields.io/badge/Java-21-red?logo=openjdk)
![Maven](https://img.shields.io/badge/Apache%20Maven-Build-blue?logo=apachemaven)
![MySQL](https://img.shields.io/badge/MySQL-8.0.33-4479A1?logo=mysql)
![HikariCP](https://img.shields.io/badge/HikariCP-5.1.0-31A8FF)
![SLF4J](https://img.shields.io/badge/SLF4J-2.0.9-lightgrey)
![Estado](https://img.shields.io/badge/Estado-En%20desarrollo-yellow)

## Estructura completa del proyecto

```
perez_iris_techdam/
├── .gitignore
├── pom.xml
├── scripts/
│   └── techdam_completo.sql
├── src/
│   └── main/
│       ├── java/
│       │   ├── Main.java
│       │   ├── config/
│       │   │   └── DatabaseConfig.java
│       │   ├── controller/
│       │   │   ├── ControladorEmpleados.java
│       │   │   ├── ControladorPrincipal.java
│       │   │   └── ControladorProyectos.java
│       │   ├── dao/
│       │   │   ├── AsignacionDAO.java
│       │   │   ├── EmpleadoDAO.java
│       │   │   └── ProyectoDAO.java
│       │   ├── model/
│       │   │   ├── Asignacion.java
│       │   │   ├── Empleado.java
│       │   │   └── Proyecto.java
│       │   ├── service/
│       │   │   ├── FuncionesService.java
│       │   │   ├── ProcedimientosService.java
│       │   │   └── TransaccionesService.java
│       │   ├── utils/
│       │   │   └── Utils.java
│       │   └── view/
│       │       ├── Menus.java
│       │       ├── VistaEmpleados.java
│       │       └── VistaProyectos.java
│       └── resources/
│           ├── TechDAM_IrisPerez.png
│           └── db.properties
└── .idea/
    (archivos internos de configuración del IDE)
```

Notas:
- El script SQL principal está en `scripts/techdam_completo.sql`.
- El archivo de propiedades de conexión está en `src/main/resources/db.properties`.

## Tecnologías y dependencias (pom.xml)

- Java 21 (compilación `maven.compiler.source` / `target`)
- MySQL Connector/J 8.0.33
- HikariCP 5.1.0 (pool de conexiones)
- SLF4J API y Simple Logger 2.0.9 (logging sencillo)

## Objetivo del proyecto

Aplicación de consola que gestiona entidades de un dominio (empleados, proyectos, asignaciones) usando JDBC avanzado: consultas, procedimientos, funciones, transacciones y un pool de conexiones para optimizar acceso a la base de datos.

## Requisitos previos

1. JDK 21 instalado.
2. Maven instalado (3.8 o superior).
3. Servidor MySQL activo.
4. Base de datos creada (por ejemplo: `techdam_db`).
5. Usuario con permisos sobre esa base (por ejemplo `techuser` con contraseña propia).

## Configuración de la Base de Datos

1. Crea la base de datos (si aún no existe).
2. Crea o verifica el usuario con permisos suficientes (SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER).
3. Abre el archivo `scripts/techdam_completo.sql` y ejecútalo en tu servidor MySQL:
   - Primero crea tablas y relaciones (el script ya las incluye).
   - Inserta los datos iniciales (incluidos en el mismo script si procede).
4. Verifica que las tablas y datos se han creado correctamente.

## Archivo de configuración de conexión

En `src/main/resources/db.properties` se declaran los parámetros de conexión (URL, usuario, contraseña). Ajusta:
- Host (por defecto suele ser `localhost`)
- Puerto (por defecto `3306`)
- Nombre de la base de datos (el que hayas creado)
- Usuario y contraseña

Guarda el archivo tras los cambios. No es necesario recompilar solo por modificar este archivo si lo mantienes en `resources`.

## Pasos para construir y ejecutar

1. Coloca la carpeta `perez_iris_techdam` completa en tu entorno (clonando el repositorio o descargando el ZIP).
2. Asegúrate de haber configurado la base de datos y ejecutado el script SQL.
3. Ajusta `db.properties` con tus credenciales reales.
4. Desde la raíz del proyecto (donde está `pom.xml`) ejecuta la construcción con Maven (generará el artefacto en `target/`).
5. Localiza el archivo JAR generado (nombre similar a `perez_iris_techdam-1.0-SNAPSHOT.jar`).
6. Ejecuta la aplicación indicando la clase principal (en este proyecto es `Main` en el paquete raíz). Si utilizas un IDE (IntelliJ, Eclipse), puedes ejecutar directamente sin usar el JAR.
7. Observa la consola: se mostrará el menú principal y podrás navegar por las opciones (gestión de empleados, proyectos, asignaciones, funciones, procedimientos, transacciones, etc.).

## Flujo típico de uso

1. Inicio de la aplicación: se carga la configuración y se inicializa el pool de conexiones (HikariCP).
2. Presentación de menús: la clase `Menus` y las vistas (`VistaEmpleados`, `VistaProyectos`) guían las interacciones.
3. Selección de operaciones: los controladores (`ControladorEmpleados`, `ControladorProyectos`, etc.) coordinan la lógica.
4. Acceso a datos: las clases DAO (`EmpleadoDAO`, `ProyectoDAO`, `AsignacionDAO`) ejecutan consultas y actualizaciones usando JDBC.
5. Operaciones avanzadas:
   - Procedimientos almacenados: manejados por `ProcedimientosService`.
   - Funciones: gestionadas por `FuncionesService`.
   - Transacciones: orquestadas por `TransaccionesService`.
6. Salida: al finalizar, se cierran recursos y el pool se libera automáticamente.

## ¿Necesita base de datos? Sí

La aplicación no funcionará sin la base de datos preparada. Si la conexión falla:
- Verifica credenciales en `db.properties`.
- Comprueba que el servidor MySQL está activo.
- Asegura que el puerto y host son correctos.
- Revisa que el script SQL se ejecutó correctamente (tablas presentes).

## Variables de entorno (opcional)

Si prefieres no guardar credenciales directamente en `db.properties`, puedes:
- Crear un usuario de solo lectura/escritura limitado.
- Externalizar contraseñas mediante tu IDE o sistema (esto requiere adaptar el código para leer variables en lugar del archivo de propiedades).

## Logging

La aplicación usa SLF4J con implementación simple:
- Verás mensajes informativos y de errores en la consola.
- Sirven para detectar problemas de conexión, transacciones fallidas o parámetros inválidos.

## Solución de problemas

| Problema | Causa probable | Acción |
|----------|----------------|--------|
| Error de conexión inicial | URL, usuario o contraseña incorrectos | Revisa `db.properties`. |
| Tablas vacías | Script SQL no ejecutado | Ejecuta `techdam_completo.sql`. |
| Lentitud | Demasiadas operaciones seguidas | Revisa uso de transacciones y confirma que no hay loops innecesarios. |
| Datos incoherentes | Faltan validaciones | Comprueba la lógica en DAOs y servicios. |
| Cierre inesperado | Excepción no controlada | Mira el último mensaje de log antes del cierre. |

## Resumen rápido

1. Configura MySQL y ejecuta el script SQL.
2. Ajusta `db.properties` con tus credenciales.
3. Construye el proyecto con Maven.
4. Ejecuta la clase principal (Main).
5. Usa los menús para interactuar con las entidades y operaciones avanzadas.
