# 🎓 Mito Academy — API REST de Gestión de Matrículas

Aplicación backend RESTful desarrollada con **Java 25** y **Spring Boot 4.1.0** para el control de registros de matrícula de estudiantes, cursos y usuarios con autenticación **JWT**, asistida y potenciada mediante agentes de IA con **Google Antigravity**.

---

## 🛠️ Tecnologías Utilizadas

### Backend y Persistencia
* **Java 25:** Uso de características modernas del lenguaje y soporte nativo para *Virtual Threads* (`spring.threads.virtual.enabled=true`).
* **Spring Boot 4.1.0:**
  * `spring-boot-starter-webmvc`: Exposición de servicios RESTful.
  * `spring-boot-starter-data-jpa`: Persistencia de datos con Hibernate / JPA.
  * `spring-boot-starter-validation`: Validación declarativa de DTOs con Jakarta Validation (`@Valid`, `@NotNull`, `@Size`, etc.).
  * `spring-boot-starter-security`: Seguridad de endpoints y control de acceso.
* **PostgreSQL:** Motor de base de datos relacional (`org.postgresql:postgresql`).
* **JSON Web Token (JJWT 0.12.6):** Autenticación *stateless* firmada con HMAC-SHA (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`).
* **ModelMapper 3.2.6:** Conversión bidireccional entre Entidades JPA y objetos DTO (`defaultMapper` y `matriculaMapper`).
* **Project Lombok:** Reducción de código repetitivo (*boilerplate*) mediante anotaciones (`@Data`, `@RequiredArgsConstructor`, `@AllArgsConstructor`, `@NoArgsConstructor`).
* **Testing:** JUnit 5, Mockito, AssertJ y `spring-boot-starter-webmvc-test` / `spring-security-test`.
* **Gestor de Dependencias:** Apache Maven.

### Inteligencia Artificial y Desarrollo Agéntico
* **Google Antigravity:** Plataforma y asistente de programación agéntica utilizada para el diseño, generación de código en capas, revisión estática, pruebas unitarias y automatización mediante **Skills** y **Subagentes** personalizados en el directorio `.agents/`.

---

## 🚀 Clonación del Repositorio y Configuración desde Cero

Sigue estos pasos para clonar el proyecto e inicializarlo en tu entorno local desde cero.

### 1. Prerrequisitos
Asegúrate de tener instaladas las siguientes herramientas:
1. **Git:** Para clonar el repositorio.
2. **JDK 25:** Configurado en tu variable de entorno `JAVA_HOME`.
3. **Apache Maven 3.9+:** (O puedes usar el wrapper incluido si dispones de él / tu IDE como Eclipse, IntelliJ IDEA o Antigravity IDE).
4. **PostgreSQL (v14+):** Ejecutándose localmente en el puerto `5432` (por defecto).
5. **Postman:** Para probar los endpoints de la API.

### 2. Clonar el Repositorio desde Git
Abre tu terminal (PowerShell, CMD o Git Bash) y ejecuta:

```bash
git clone https://github.com/Danigomez1111/mito-academy.git
cd mito-academy
```

### 3. Configurar la Base de Datos en PostgreSQL
1. Abre **pgAdmin** o tu consola `psql`.
2. Crea la base de datos llamada `mito_academy`:
   ```sql
   CREATE DATABASE mito_academy;
   ```
> **Nota:** No necesitas ejecutar scripts SQL manuales para las tablas. Gracias a la configuración `spring.jpa.hibernate.ddl-auto: update` y a la clase `SecurityDataInitializer`, Hibernate creará automáticamente todas las tablas (`estudiante`, `curso`, `matricula`, `detalle_matricula`, `rol`, `usuario`) e insertará los usuarios iniciales al arrancar la aplicación.

---

## 🔐 Creación y Configuración del Archivo `.env`

Por motivos de seguridad, las credenciales de la base de datos y la semilla secreta para firmar los tokens JWT no se suben al repositorio (están excluidas en `.gitignore`) y se cargan dinámicamente en `src/main/resources/application.yaml` mediante:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
```

### Pasos para crear el `.env`
1. En la **raíz del proyecto** (`mito-academy/`), crea un archivo llamado exactamente **`.env`**.
2. Agrega el siguiente contenido reemplazando con tus credenciales locales de PostgreSQL:

```env
DB_USERNAME=postgres
DB_PASSWORD=tu_password_de_postgres
JWT_SECRET=MiClaveSecretaParaElProyectoHotel2026DeMitoAcademy
```

*(Opcionalmente puedes definir `DB_URL=jdbc:postgresql://localhost:5432/mito_academy` si tu servidor PostgreSQL utiliza otro puerto o host).*

| Variable | Descripción | Valor por Defecto / Ejemplo |
| :--- | :--- | :--- |
| `DB_URL` | URL de conexión JDBC a PostgreSQL | `jdbc:postgresql://localhost:5432/mito_academy` |
| `DB_USERNAME` | Usuario de PostgreSQL | `postgres` |
| `DB_PASSWORD` | Contraseña del usuario de PostgreSQL | *(Definida en tu `.env`)* |
| `JWT_SECRET` | Clave secreta para firmar tokens JWT (mínimo 32 caracteres recomendado) | `MiClaveSecretaParaElProyectoHotel2026DeMitoAcademy` |

---

## ▶️ Compilación y Ejecución del Proyecto

Una vez creada la base de datos y el archivo `.env`, puedes levantar el servidor de cualquiera de las siguientes formas:

### Opción A: Desde la Terminal con Maven
```bash
# 1. Limpiar y compilar el proyecto
mvn clean compile

# 2. Ejecutar las pruebas unitarias (opcional)
mvn test

# 3. Levantar el servidor Spring Boot
mvn spring-boot:run
```

### Opción B: Desde Eclipse / Spring Tool Suite / Antigravity IDE
1. Abre tu IDE y selecciona **File → Import → Maven → Existing Maven Projects**.
2. Selecciona la carpeta raíz `mito-academy` y haz clic en **Finish**.
3. Haz clic derecho sobre la clase principal `src/main/java/com/mitocode/MitoAcademyApplication.java` y selecciona **Run As → Java Application** (o **Spring Boot App**).

El servidor iniciará por defecto en: **`http://localhost:8080`**

---

## 📬 Colección de Postman (Descargar e Importar)

En el repositorio se incluye la colección oficial de Postman lista para probar todos los endpoints del proyecto.

### Ubicación del archivo
El archivo JSON de la colección se encuentra en la carpeta `collections-postman/`:
```text
collections-postman/MitoAcademy - Trabajo Práctico Final.postman_collection.json
```

### Cómo Descargarla e Importarla en Postman
1. **Obtener el archivo:**
   * Si ya clonaste el repositorio, búscalo directamente en la carpeta `collections-postman/` de tu proyecto local.
   * Si estás en GitHub desde el navegador, entra a la carpeta `collections-postman/`, abre el archivo `MitoAcademy - Trabajo Práctico Final.postman_collection.json` y haz clic en el botón **Download raw file** (icono de descarga arriba a la derecha).
2. **Importar en Postman:**
   * Abre **Postman**.
   * Haz clic en el botón **Import** (ubicado en la esquina superior izquierda, junto a *New*).
   * Arrastra y suelta el archivo `MitoAcademy - Trabajo Práctico Final.postman_collection.json` o selecciónalo desde tu explorador de archivos.
   * Verás la colección **"MitoAcademy - Trabajo Práctico Final"** organizada en 3 carpetas:
     1. `1. Estudiantes` (CRUD completo + Query funcional de estudiantes ordenados por edad descendente).
     2. `2. Cursos` (CRUD completo).
     3. `3. Matrículas` (CRUD maestro-detalle + Query funcional de relación Cursos y Estudiantes).

### Autenticación JWT en Postman
Al iniciar la aplicación, `SecurityDataInitializer` crea automáticamente dos usuarios de prueba en la base de datos:

| Usuario | Contraseña | Rol |
| :--- | :--- | :--- |
| `admin` | `123` | `ADMIN` |
| `mito` | `123` | `USER` |

Para consumir los endpoints protegidos:
1. Realiza una petición `POST` a `http://localhost:8080/login` con el siguiente Body JSON:
   ```json
   {
     "username": "admin",
     "password": "123"
   }
   ```
2. Copia el valor de `jwttoken` devuelto en la respuesta.
3. En Postman, ve a la pestaña **Authorization** de la petición (o de la colección completa), selecciona el tipo **Bearer Token** y pega el token obtenido.

---

## 🤖 Uso de Antigravity: Creación de Agentes, Skills y Subagentes

Este proyecto fue desarrollado utilizando **Google Antigravity**, aprovechando su sistema de personalización agéntica ubicado en el directorio `.agents/` en la raíz del repositorio.

### Estructura Agéntica del Proyecto (`.agents/` y `specs/`)
```text
mito-academy/
├── specs/
│   └── SPEC.md                  # Especificación Técnica y Funcional (Spec-Driven Development)
└── .agents/
    ├── skills/
    │   └── dr-jskill/           # Skill especializada en proyectos Java 25 + Spring Boot 4
    │       └── SKILL.md
    └── subagents/
        ├── crud-builder.md      # Subagente generador de capas CRUD completas
        ├── code-review.md       # Subagente revisor de código y convenciones de arquitectura
        └── code-tester.md       # Subagente creador de pruebas unitarias (JUnit 5 + Mockito)
```

### 0. Spec-Driven Development (`specs/SPEC.md`) y Cómo Probar que Funcione

El proyecto incluye el archivo **`specs/SPEC.md`**, donde se documenta el contrato de entrada (*Input Contract*) de todas las verticales (`Estudiante`, `Curso`, `Matricula`, `DetalleMatricula` y Seguridad `JWT`), junto con una plantilla para crear nuevas entidades.

#### A. Cómo probar un Spec con los Subagentes en Antigravity
1. Abre o edita `specs/SPEC.md` (o crea un nuevo archivo en `specs/`, por ejemplo `specs/profesor-spec.md`) usando la plantilla de la sección 4 de `specs/SPEC.md`.
2. En el chat de **Antigravity**, ejecuta el siguiente prompt para que los tres subagentes trabajen en cadena a partir de tu Spec:
   > *"Lee la especificación en `specs/SPEC.md`. Usa el subagente `crud-builder` para generar las capas de la entidad indicada, luego ejecuta `code-tester` para crear sus pruebas unitarias y finalmente pasa `code-reviewer` para verificar que cumpla con todas las convenciones del proyecto."*

#### B. Cómo probar las Especificaciones de Pruebas Unitarias (`src/test/`)
Para comprobar que todos los controladores y servicios cumplen con la especificación sin errores, ejecuta la suite de pruebas unitarias (JUnit 5 + Mockito + AssertJ):

```bash
# Ejecutar todos los tests del proyecto
mvn test

# Ejecutar únicamente los tests unitarios de servicios y controladores (sin requerir PostgreSQL activo)
mvn test -Dtest="*ServiceImplTest,*ControllerTest,EstudianteTest"
```

### 1. ¿Qué son los Agentes y Subagentes en Antigravity?
* **Agente Principal (Antigravity):** Es el asistente principal con el que interactúas en el chat. Coordina tareas, lee el contexto del proyecto, ejecuta comandos y puede delegar trabajo especializado.
* **Skills (`.agents/skills/<nombre>/SKILL.md`):** Son módulos de conocimiento que se cargan bajo demanda (*progressive disclosure*) para enseñar al agente flujos complejos (como `dr-jskill` para arquitectura con Spring Boot 4 y Java 25).
* **Subagentes (`.agents/subagents/<nombre>.md`):** Son agentes especializados que se ejecutan en su propio contexto aislado para resolver tareas específicas sin saturar la ventana de contexto del agente principal.

### 2. Subagentes Incluidos en este Proyecto
1. **`crud-builder` (`.agents/subagents/crud-builder.md`):**
   * **Rol:** Ingeniero Backend Senior en Spring Boot.
   * **Función:** Genera el stack CRUD completo de extremo a extremo (`Model`, `DTO`, `Repo`, `Service`, `ServiceImpl`, `Controller` y `MapperConfig`) reutilizando `IGenericRepo<T, ID>` y `CRUDImpl<T, ID>` tomando como referencia canónica la vertical `Estudiante`.
2. **`code-reviewer` (`.agents/subagents/code-review.md`):**
   * **Rol:** Revisor de código y arquitectura.
   * **Función:** Audita controladores (`/v1`, códigos HTTP 200/201/204, `@Valid`), persistencia (`@EqualsAndHashCode(onlyExplicitlyIncluded = true)`, cascadas en `Matricula`), servicios y mapeos bidireccionales en `ModelMapper`.
3. **`code-tester` (`.agents/subagents/code-tester.md`):**
   * **Rol:** Ingeniero de QA y Testing en Spring Boot 4.
   * **Función:** Crea y mantiene pruebas unitarias puras con **JUnit 5, Mockito y AssertJ** para las capas de servicio y controlador (usando `tools.jackson.databind.ObjectMapper` de Jackson 3).

### 3. ¿Cómo Crear un Nuevo Subagente o Skill en Antigravity?

#### A. Crear un Subagente mediante archivo Markdown (Persistente en el Repositorio)
1. Crea un archivo `.md` dentro de la carpeta `.agents/subagents/`, por ejemplo `.agents/subagents/mi-subagente.md`.
2. Define el **YAML Frontmatter** en la parte superior con el nombre, descripción, herramientas permitidas y modelo, seguido del *System Prompt* con las instrucciones:

```markdown
---
name: security-auditor
description: Audita la configuración de Spring Security, filtros JWT y permisos de endpoints.
tools: Read, Grep, Glob, Bash
model: sonnet
effort: high
---

Eres un especialista en ciberseguridad para aplicaciones Spring Boot 4.

## Contexto Operativo
Revisa el paquete `com.mitocode.security` y `WebSecurityConfig`...

## Reglas de Revisión
1. Verificar que ningún secreto esté hardcodeado en `application.yaml`.
2. Confirmar que las contraseñas se almacenen con `BCryptPasswordEncoder`.
```

#### B. Crear e Invocar Subagentes Dinámicamente desde el Chat de Antigravity
También puedes pedirle directamente a **Antigravity** en el chat que defina o invoque un subagente durante tu sesión:
* **Definir en caliente:** Antigravity utiliza su herramienta interna `define_subagent` especificando `Name`, `Description`, `SystemPrompt` y permisos (`EnableWriteTools`, `EnableMCPTools`).
* **Invocar en paralelo:** Puedes pedirle en el prompt:
  > *"Usa el subagente `crud-builder` para crear la entidad Profesor, luego ejecuta `code-tester` para generar sus tests unitarios y finalmente pasa `code-reviewer` para validar todo."*

#### C. Crear una Skill Personalizada (`.agents/skills/`)
1. Crea una carpeta dentro de `.agents/skills/<nombre-skill>/`.
2. Dentro de ella, crea un archivo obligatorio llamado `SKILL.md` con su encabezado YAML:
   ```markdown
   ---
   name: mi-skill-spring
   description: Guía paso a paso para estandarizar reportes y consultas funcionales en Mito Academy.
   ---

   # Instrucciones de la Skill
   Aquí detallas los pasos, plantillas de código y convenciones que Antigravity cargará automáticamente cuando detecte una tarea relacionada.
   ```
3. Antigravity descubrirá automáticamente todas las skills y reglas ubicadas en `.agents/` al abrir el espacio de trabajo.
