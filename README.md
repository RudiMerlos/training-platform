## Proyecto de Ejercicio: Plataforma de Formación Interna para Empleados

### 🌍 Descripción General
Una aplicación backend que permita a una empresa gestionar la formación interna de sus empleados,
con las siguientes funcionalidades:

- CRUD de empleados
- CRUD de cursos de formación
- Asignación de cursos a empleados
- Consulta del estado de la formación

Esto nos permite tocar casi todos los puntos del roadmap.

### 🚀 Tecnologías y herramientas
- Java 17+
- Spring Boot (Data, Security, Validation, Web, Cache, Actuator)
- Maven o Gradle
- PostgreSQL (o H2 para tests)
- JPA/Hibernate
- Swagger / OpenAPI
- Docker
- Testcontainers, JUnit5, Mockito
- Redis (cache)
- Kafka (mensajería simulada)

### 🔍 Entidades principales
- Employee : id, name, email, department
- Course : id, name, description, expirationDays
- EmployeeCourse : id, employee_id, course_id, assigned_on, status (ASSIGNED, COMPLETED,
EXPIRED)

### 📊 Funcionalidades requeridas
#### CRUD y negocio
- Crear/leer/actualizar/borrar empleados y cursos
- Asignar cursos a empleados
- Marcar como completado
- Ver cursos pendientes por empleado
- Validar que un curso se ha completado antes de su vencimiento

#### Seguridad
- Roles: ADMIN , USER
- Autenticación JWT
- Autorización por rol en endpoints REST

#### Documentación
- Swagger/OpenAPI generado automáticamente

#### Logging y observabilidad
- Configurar SLF4J + Logback
- Usar @Slf4j para registrar eventos importantes
- Exponer /actuator/health , /actuator/metrics

#### Configuración y entornos
- application-dev.yml , application-prod.yml , application-test.yml
- Uso de perfiles Spring

#### Validaciones y errores
- Validar que email es válido, campos obligatorios
- Manejar errores con @ControllerAdvice

#### Tests
- Unitarios (EmployeeService, CourseService)
- Integración (con H2 o Testcontainers)

#### Docker y despliegue
- Dockerfile
- docker-compose con PostgreSQL + Redis

#### Caché
- Cachear cursos por ID (EhCache o Caffeine)

#### Mensajería (opcional)
- Al asignar curso, enviar evento a Kafka simulado (log de auditoría, por ejemplo)

### 🏆 Evaluación del ejercicio
Para evaluar el ejercicio completo:
1. Arquitectura: organización limpia, separación por capas, paquetes
2. Calidad de código: principios SOLID, testable, legible
3. Spring: uso correcto de perfiles, configuraciones, seguridad, JPA, validaciones
4. Infraestructura: contenedores, Docker, logs
5. Tests: cobertura, aislamiento, uso de mocks y base real
6. API: buena estructura REST, documentación clara, buen manejo de errores

### ✅ Resultado final esperado
Un repositorio con: - Carpeta /src bien estructurada - Scripts o docker-compose.yml - README
explicando: - Tecnologías usadas - Cómo levantarlo en local - Endpoints principales (con Swagger) -
Usuarios de prueba y roles - Áreas cubiertas del roadmap