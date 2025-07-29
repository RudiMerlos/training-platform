## Proyecto de Ejercicio: Plataforma de Formación Interna para Empleados

### Descripción General
Aplicación backend que permita a una empresa gestionar la formación interna de sus empleados, con las siguientes
funcionalidades:

- CRUD de empleados
- CRUD de cursos de formación
- Asignación de cursos a empleados
- Consulta del estado de la formación

### Tecnologías y herramientas
- Java 21
- Spring Boot (Data, Security, Validation, Web, Cache, Actuator)
- Gradle
- PostgreSQL (y H2 para tests)
- JPA/Hibernate
- Swagger/OpenAPI
- Docker
- JUnit5, Mockito
- Redis (cache)

### Enpoints principales
- Login, registro y listado de roles de usuarios.
- Crear/leer/actualizar/borrar empleados y cursos
- Ver todos los cursos de un empleado
- Ver cursos pendientes por empleado
- Asignar cursos a empleados
- Marcar cursos como completado/expirado por empleado

### Seguridad
- Roles: ADMIN , USER
- Autenticación JWT
- Autorización por rol en endpoints REST

### Documentación
- Swagger/OpenAPI (configurado para autenticación por token)

### Logging y observabilidad
- SLF4J + Logback
- Spring actuator

### Configuración y entornos
- application-dev.yml , application-prod.yml , application-test.yml

### Validaciones y errores
- Se validan los campos obligatorios así como la correcta definición de los emails.
- Manejo de errores con @ControllerAdvice

### Caché
- Se cachean empleados y cursos con Caffeine y Redis.

### Tests
- Unitarios (Servicios)
- Integración (con H2)

### Docker y despliegue
- Dockerfile
- docker-compose para el despliegue con PostgreSQL + Redis
- Instrucciones para el deploy:
  - Está configurado para desplegarse el perfil **prod**.
  - La clave secreta para generar el token (JWT) y la contraseña de BD están parametrizadas en el fichero **.env**. En
    este caso se distribuye en el proyecto pero por cuestiones de seguridad debería incluirse en el **.gitignore**.

        $docker compose up --build
