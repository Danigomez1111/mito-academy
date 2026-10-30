# Documentación del Proyecto — Mito Academy

Aplicación backend RESTful desarrollada con **Spring Boot 4.1.0** y **Java 25** para el control de registros de matrícula de estudiantes, cursos y sus respectivos detalles.

---

## 1. Resumen de la Arquitectura del Proyecto

El proyecto sigue una **arquitectura en capas (Layered Architecture)** bajo el paquete base `com.mitocode`, aplicando el patrón de diseño genérico para operaciones CRUD y separación entre entidades de persistencia y objetos de transferencia de datos (DTO).

### Stack Tecnológico
- **Java:** 25 (con soporte para *Virtual Threads* habilitado y características modernas como variables sin nombre `_`).
- **Framework:** Spring Boot 4.1.0 (`spring-boot-starter-webmvc`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`).
- **Base de Datos:** PostgreSQL (`org.postgresql:postgresql`).
- **Mapeo de Objetos:** ModelMapper 3.2.6.
- **Utilitarios:** Project Lombok.

### Estructura de Capas y Paquetes (`com.mitocode`)

```text
com.mitocode
├── config/          # Configuración de beans (ModelMapper: defaultMapper y matriculaMapper)
├── controller/      # Controladores REST (@RestController) y exposición de endpoints
├── dto/             # Objetos de Transferencia de Datos con validaciones (Jakarta Validation)
├── exception/       # Manejo centralizado de excepciones (@RestControllerAdvice)
├── model/           # Entidades JPA (@Entity) mapeadas a tablas en PostgreSQL
├── repo/            # Repositorios de acceso a datos (Spring Data JPA / IGenericRepo)
└── service/         # Interfaces de lógica de negocio (ICRUD)
    └── impl/        # Implementaciones de servicios (CRUDImpl y programación funcional)
```

1. **Capa de Presentación (`controller` y `dto`):**
   - Recibe las peticiones HTTP en formato JSON, valida los datos de entrada mediante `@Valid` en los DTOs (`EstudianteDTO`, `CursoDTO`, `MatriculaDTO`, `DetalleMatriculaDTO`) y convierte entre DTOs y Entidades usando `ModelMapper`.
   - Endpoints principales:
     - `/v1/estudiantes`: CRUD de estudiantes y listado ordenado por edad de forma descendente (`GET /v1/estudiantes/ordenados-edad`).
     - `/v1/cursos`: CRUD de cursos.
     - `/v1/matriculas`: CRUD de matrículas (maestro-detalle) y reporte de cursos con sus estudiantes matriculados (`GET /v1/matriculas/cursos-estudiantes`).

2. **Capa de Negocio (`service` y `service.impl`):**
   - Define operaciones genéricas mediante la interfaz `ICRUD<T, ID>` y su clase abstracta `CRUDImpl<T, ID>`, la cual utiliza reflexión de Java para validar y asignar identificadores en actualizaciones.
   - Implementa consultas específicas con **Streams y Programación Funcional** en `EstudianteServiceImpl` y `MatriculaServiceImpl`.

3. **Capa de Persistencia (`repo` y `model`):**
   - `IGenericRepo<T, ID>` extiende `JpaRepository<T, ID>` (`@NoRepositoryBean`) para estandarizar los repositorios de `Estudiante`, `Curso` y `Matricula`.
   - Relación maestro-detalle entre `Matricula` y `DetalleMatricula` configurada con `CascadeType.ALL` y `orphanRemoval = true`.

4. **Capa Transversal (`config` y `exception`):**
   - `MapperConfig`: Define `defaultMapper` y `matriculaMapper` (con un `PostConverter` para vincular la referencia padre `Matricula` en cada `DetalleMatricula`).
   - `GlobalErrorHandler`: Captura globalmente errores (`ModelNotFoundException`, errores de validación `@Valid`, etc.) y devuelve respuestas estandarizadas mediante `CustomErrorResponse`.

---

## 2. Conexión a PostgreSQL y Variables de Entorno (`.env`)

La configuración de conexión a la base de datos y seguridad JWT se encuentra en `src/main/resources/application.yaml`:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
  jpa:
    database: postgresql
    show-sql: false
    hibernate:
      ddl-auto: update
  datasource:
    driver-class-name: org.postgresql.Driver
    url: ${DB_URL:jdbc:postgresql://localhost:5432/mito_academy}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  threads:
    virtual:
      enabled: true

jwt:
  secret: ${JWT_SECRET}
  expiration: 18000000
```

### Configuración del archivo `.env` y Seguridad en Git
Para evitar exponer credenciales sensibles o claves secretas directamente en `application.yaml`, el proyecto carga las variables desde un archivo llamado exactamente **`.env`** ubicado en la raíz del proyecto (gracias a `spring.config.import: optional:file:.env[.properties]`).

1. **Crear el archivo `.env`** en la raíz del proyecto (`mito-academy/.env`) con el siguiente contenido:
   ```env
   DB_USERNAME=postgres
   DB_PASSWORD=tu_contraseña_real
   JWT_SECRET=el_hash_largo_de_sha512
   ```
2. **Exclusión en `.gitignore`:**
   El archivo `.env` está incluido en el archivo `.gitignore` para que **nunca se suba al repositorio Git**, protegiendo así las contraseñas de base de datos y la firma de los tokens JWT.

### Requisitos y Preparación de la Base de Datos
1. Tener **PostgreSQL** instalado y ejecutándose en el puerto `5432` (por defecto).
2. Crear la base de datos llamada `mito_academy` antes de iniciar la aplicación:
   ```sql
   CREATE DATABASE mito_academy;
   ```
3. **Esquema automático:** Gracias a la propiedad `spring.jpa.hibernate.ddl-auto: update`, Hibernate creará o actualizará automáticamente las tablas (`estudiante`, `curso`, `matricula`, `detalle_matricula`) y sus llaves foráneas al iniciar la aplicación.
4. **Detalle de Variables de Entorno:**
   - `DB_URL`: URL JDBC (por defecto `jdbc:postgresql://localhost:5432/mito_academy`).
   - `DB_USERNAME`: Usuario de PostgreSQL definido en `.env`.
   - `DB_PASSWORD`: Contraseña real de PostgreSQL definida en `.env`.
   - `JWT_SECRET`: Clave secreta (hash largo para SHA-512) utilizada para firmar y validar los tokens JWT, definida en `.env`.

---

## 3. Pasos para Ejecutar la Aplicación con Maven

### Prerrequisitos
- **JDK 25** instalado y configurado en la variable de entorno `JAVA_HOME`.
- **Apache Maven** instalado (o utilizar el *Maven Wrapper* `mvnw` incluido en el proyecto).
- Base de datos `mito_academy` creada en PostgreSQL.

### Ejecución paso a paso

1. **Ubicarse en el directorio del proyecto:**
   ```bash
   cd mito-academy
   ```

2. **Limpiar y compilar el proyecto:**
   - Usando Maven Wrapper (Windows PowerShell / CMD):
     ```powershell
     .\mvnw.cmd clean compile
     ```
   - Usando Maven Wrapper (Linux / macOS / Git Bash):
     ```bash
     ./mvnw clean compile
     ```
   - Usando Maven global:
     ```bash
     mvn clean compile
     ```

3. **Ejecutar la aplicación directamente con el plugin de Spring Boot:**
   - En Windows:
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
   - En Linux / macOS:
     ```bash
     ./mvnw spring-boot:run
     ```
   - Con Maven global:
     ```bash
     mvn spring-boot:run
     ```

4. **(Opcional) Empaquetar y ejecutar el archivo JAR:**
   ```powershell
   .\mvnw.cmd clean package -DskipTests
   java -jar target/mito-academy-0.0.1-SNAPSHOT.jar
   ```

Una vez iniciada la aplicación, el servidor estará disponible por defecto en `http://localhost:8080`.
