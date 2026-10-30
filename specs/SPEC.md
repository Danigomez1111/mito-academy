# 📋 Especificación Técnica (SPEC) — Mito Academy

Este documento define la especificación técnica y funcional (**Spec-Driven Development**) del proyecto **Mito Academy** bajo el paquete base `com.mitocode`. Sirve como fuente de verdad (**Input Contract**) para los subagentes de **Google Antigravity** (`.agents/subagents/crud-builder.md`, `code-tester.md` y `code-review.md`).

---

## 1. Convenciones Globales de Arquitectura

* **Paquete Base:** `com.mitocode`
* **Java / Spring Boot:** Java 25 (con Virtual Threads) + Spring Boot 4.1.0.
* **Abstracciones Compartidas (No duplicar):**
  * `repo.IGenericRepo<T, ID>` (`@NoRepositoryBean` que extiende `JpaRepository<T, ID>`).
  * `service.ICRUD<T, ID>` (`save`, `update`, `findAll`, `findById`, `delete`).
  * `service.impl.CRUDImpl<T, ID>` (resuelve el setter del ID por reflexión `"setId" + simpleClassName` y lanza `ModelNotFoundException` cuando el ID no existe).
  * `exception.GlobalErrorHandler` (`@RestControllerAdvice` centralizado con `CustomErrorResponse`).
* **Inyección de Dependencias:** Por constructor usando `private final` + `@RequiredArgsConstructor` (prohibido `@Autowired` en atributos).
* **Códigos de Estado HTTP en Controladores:**
  * `200 OK`: `GET` (listar / obtener por ID) y `PUT` (actualizar).
  * `201 CREATED`: `POST` (registrar).
  * `204 NO CONTENT`: `DELETE` (eliminar).

---

## 2. Especificación de Dominios (Verticales CRUD)

### 2.1 Vertical: Estudiante
* **Entidad (`com.mitocode.model.Estudiante`):**
  * `idEstudiante`: `Integer` (`@Id`, `@GeneratedValue(IDENTITY)`, `@EqualsAndHashCode.Include`).
  * `nombres`: `String` (`nullable = false`, `length = 100`).
  * `apellidos`: `String` (`nullable = false`, `length = 100`).
  * `dni`: `String` (`nullable = false`, `length = 10`, `unique = true`).
  * `edad`: `int` (`nullable = false`).
* **DTO y Validaciones (`com.mitocode.dto.EstudianteDTO`):**
  * `@JsonInclude(JsonInclude.Include.NON_NULL)`
  * `nombres`: `@NotNull`, `@NotEmpty`, `@Size(min = 3, max = 100)`
  * `apellidos`: `@NotNull`, `@NotEmpty`, `@Size(min = 3, max = 100)`
  * `dni`: `@NotNull`, `@NotEmpty`, `@Size(min = 8, max = 10)`
  * `edad`: `@NotNull`, `@Min(1)`, `@Max(120)`
* **Endpoints (`com.mitocode.controller.EstudianteController`):**
  * Ruta base: `/v1/estudiantes`
  * Operaciones CRUD estándar (`GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`).
  * **Endpoint Funcional:** `GET /v1/estudiantes/ordenados-edad` → Lista los estudiantes ordenados de forma descendente por `edad` usando *Java Streams* (`Comparator.comparingInt(Estudiante::getEdad).reversed()`).

---

### 2.2 Vertical: Curso
* **Entidad (`com.mitocode.model.Curso`):**
  * `idCurso`: `Integer` (`@Id`, `@GeneratedValue(IDENTITY)`, `@EqualsAndHashCode.Include`).
  * `nombre`: `String` (`nullable = false`, `length = 100`).
  * `siglas`: `String` (`nullable = false`, `length = 10`).
  * `estado`: `boolean` (`nullable = false`).
* **DTO y Validaciones (`com.mitocode.dto.CursoDTO`):**
  * `@JsonInclude(JsonInclude.Include.NON_NULL)`
  * `nombre`: `@NotNull`, `@NotEmpty`, `@Size(min = 3, max = 100)`
  * `siglas`: `@NotNull`, `@NotEmpty`, `@Size(min = 2, max = 10)`
  * `estado`: `@NotNull`
* **Endpoints (`com.mitocode.controller.CursoController`):**
  * Ruta base: `/v1/cursos`
  * Operaciones CRUD estándar (`GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`).

---

### 2.3 Vertical: Matrícula y DetalleMatrícula (Maestro-Detalle)
* **Entidad Padre (`com.mitocode.model.Matricula`):**
  * `idMatricula`: `Integer` (`@Id`, `@GeneratedValue(IDENTITY)`, `@EqualsAndHashCode.Include`).
  * `fechaMatricula`: `LocalDateTime` (`nullable = false`).
  * `estudiante`: `Estudiante` (`@ManyToOne`, `@JoinColumn(name = "id_estudiante", nullable = false, foreignKey = @ForeignKey(name = "fk_matricula_estudiante"))`).
  * `detalleMatricula`: `List<DetalleMatricula>` (`@OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL, orphanRemoval = true)`).
  * `estado`: `boolean` (`nullable = false`).
* **Entidad Hija (`com.mitocode.model.DetalleMatricula`):**
  * `idDetalleMatricula`: `Integer` (`@Id`, `@GeneratedValue(IDENTITY)`, `@EqualsAndHashCode.Include`).
  * `matricula`: `Matricula` (`@ManyToOne`, `@JoinColumn(name = "id_matricula", nullable = false, foreignKey = @ForeignKey(name = "fk_detalle_matricula"))`).
  * `curso`: `Curso` (`@ManyToOne`, `@JoinColumn(name = "id_curso", nullable = false, foreignKey = @ForeignKey(name = "fk_detalle_curso"))`).
  * `aula`: `String` (`nullable = false`, `length = 50`).
* **DTOs (`MatriculaDTO` y `DetalleMatriculaDTO`):**
  * `MatriculaDTO`: `idMatricula`, `fechaMatricula` (`@NotNull`), `estudiante` (`@NotNull EstudianteDTO`), `detalleMatricula` (`@NotNull @NotEmpty @Valid List<DetalleMatriculaDTO>`), `estado` (`@NotNull`).
  * `DetalleMatriculaDTO`: `idDetalleMatricula`, `curso` (`@NotNull CursoDTO`), `aula` (`@NotNull @NotEmpty @Size(min = 2, max = 50)`).
* **Mapper Especial (`config.MapperConfig`):**
  * Bean `@Qualifier("matriculaMapper")` con `PostConverter` para vincular cada `DetalleMatricula` con su referencia padre `Matricula` antes de persistir.
* **Endpoints (`com.mitocode.controller.MatriculaController`):**
  * Ruta base: `/v1/matriculas`
  * Operaciones CRUD estándar (`GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`).
  * **Endpoint Funcional:** `GET /v1/matriculas/cursos-estudiantes` → Retorna un `Map<String, List<String>>` relacionando el nombre del curso con la lista de nombres completos de los estudiantes matriculados usando `Stream.flatMap` y `Collectors.groupingBy`.

---

## 3. Especificación de Seguridad (Spring Security + JWT)

* **Entidades:**
  * `Rol` (`idRol`, `nombre`, `descripcion`).
  * `Usuario` (`idUsuario`, `username`, `password`, `rol`, `enabled`).
* **Inicialización de Datos (`SecurityDataInitializer`):**
  * Crea roles `ADMIN` y `USER`, y usuarios por defecto `admin` (`123`) y `mito` (`123`) cifrados con `BCryptPasswordEncoder`.
* **Autenticación (`LoginController`):**
  * Endpoint público: `POST /login`
  * Recibe `JwtRequest` (`username`, `password`) y retorna `JwtResponse` (`jwttoken`) firmado con HMAC-SHA512 usando `${JWT_SECRET}`.
* **Autorización (`WebSecurityConfig`):**
  * `/login` permitido públicamente (`permitAll()`).
  * Resto de rutas (`/v1/**`) requieren token JWT válido en la cabecera `Authorization: Bearer <token>`.

---

## 4. Plantilla para Nuevas Entidades (Input Contract para `crud-builder`)

Para agregar un nuevo módulo con **Antigravity**, copia o referencia esta plantilla en tu prompt:

```markdown
### Nueva Entidad: <NombreEntidadEnSingular>
- **PK:** `id<NombreEntidad>` (`Integer`, IDENTITY)
- **Campos:**
  - `<campo1>`: `<TipoJava>`, length = <N>, nullable = <true/false>
  - `<campo2>`: `<TipoJava>`, nullable = <true/false>
- **Validaciones DTO (`<NombreEntidad>DTO`):**
  - `<campo1>`: `@NotNull`, `@NotEmpty`, `@Size(min = X, max = Y)`
- **Ruta REST (`<NombreEntidad>Controller`):**
  - `/v1/<nombre-plural-minusculas>`
```
