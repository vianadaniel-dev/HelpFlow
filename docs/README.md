# HelpFlow - Sistema de Gestión de Soporte IT

HelpFlow es un microservicio backend robusto diseñado para gestionar tickets de soporte técnico, asignaciones y cumplimiento de SLAs (Acuerdos de Nivel de Servicio). Está construido bajo los principios de **Arquitectura Hexagonal (Puertos y Adaptadores)** y **Domain-Driven Design (DDD)**, asegurando un alto desacoplamiento y facilidad de prueba.

## 🏗️ Arquitectura Hexagonal

El proyecto está estrictamente dividido en tres capas principales para aislar la lógica de negocio de las tecnologías externas (Bases de datos, APIs REST, Seguridad, etc.).

```text
src/main/java/com/vianadanieldev/helpflow/
├── domain/                  # 1. Capa de Dominio (El corazón del software)
│   ├── exception/           # Excepciones puras de negocio (ej. InvalidStateTransitionException)
│   └── model/               # Entidades puras y Reglas de Negocio (Ticket, User, Assignment)
│
├── application/             # 2. Capa de Aplicación (Casos de Uso)
│   ├── port/                # Contratos (Interfaces)
│   │   ├── in/              # Puertos de Entrada (Lo que el sistema PUEDE HACER)
│   │   └── out/             # Puertos de Salida (Lo que el sistema NECESITA del exterior)
│   └── service/             # Implementación de los casos de uso orquestando el dominio
│
└── infrastructure/          # 3. Capa de Infraestructura (Adaptadores)
    └── adapter/
        ├── in/              # Adaptadores de Entrada (Controladores REST, GlobalExceptionHandler)
        └── out/             # Adaptadores de Salida (JPA, Repositorios, Cliente REST externo)
            ├── client/      # Integración con microservicio externo (Technician Dispatch)
            └── persistence/ # Entidades JPA, MapStruct Mappers y Spring Data
```

### Reglas de Dependencia
*   **Dominio:** No depende de NADA (Ni Spring, ni JPA, ni Lombok). Es Java puro.
*   **Aplicación:** Solo depende del Dominio.
*   **Infraestructura:** Depende de la Aplicación y del Dominio para traducir datos externos a formatos internos.

## 🚀 Stack Tecnológico

*   **Lenguaje:** Java 21
*   **Framework:** Spring Boot 3.3.4
*   **Base de Datos:** PostgreSQL 17 (vía Docker)
*   **Migraciones:** Flyway
*   **Seguridad:** Spring Security + JWT (Stateless)
*   **Mapeo de Objetos:** MapStruct + Lombok
*   **Manejo de Errores:** RFC 7807 (ProblemDetail)

## 🛠️ Prerrequisitos

Para ejecutar este proyecto localmente, necesitas tener instalado:
*   [Java 21 (JDK)](https://adoptium.net/)
*   [Docker](https://www.docker.com/) y Docker Compose
*   [Maven](https://maven.apache.org/) (opcional, el proyecto incluye el Wrapper `./mvnw`)
*   Un cliente REST como [Postman](https://www.postman.com/) o Thunder Client.

## ⚙️ Instalación y Ejecución

**1. Levantar la base de datos (PostgreSQL)**
El proyecto incluye un archivo `compose.yaml` configurado. El puerto expuesto es el `5433` para evitar conflictos con instalaciones locales de Postgres.
```bash
docker-compose up -d
```

**2. Ejecutar la aplicación**
Al arrancar, Spring Boot se conectará a Docker y Flyway creará automáticamente las tablas e insertará los **datos de prueba**.
```bash
./mvnw clean compile spring-boot:run
```
La aplicación estará disponible en `http://localhost:8080`.

## 🧪 Guía de Pruebas (API)

Flyway inyecta automáticamente los siguientes usuarios de prueba (La contraseña para ambos es `123456`):
*   **Usuario Regular:** `daniel@helpflow.com` (Puede crear tickets)
*   **Técnico:** `tecnico@helpflow.com` (Puede atender tickets)

### 1. Autenticación (Login)
Obtén el token JWT para autorizar tus peticiones.

*   **Endpoint:** `POST /auth/login`
*   **Body (JSON):**
```json
{
    "corporateEmail": "daniel@helpflow.com",
    "password": "123456"
}
```
*   **Respuesta:** Guarda el campo `"accessToken"`.

### 2. Registrar un Ticket
Crea un ticket de soporte utilizando el token del usuario regular.

*   **Endpoint:** `POST /api/v1/tickets`
*   **Headers:** `Authorization: Bearer <TU_TOKEN>`
*   **Body (JSON):**
```json
{
    "requesterId": "11111111-1111-1111-1111-111111111111",
    "title": "Problema con mi equipo",
    "description": "El monitor no da imagen",
    "category": "HARDWARE",
    "headquarters": "BARRANQUILLA"
}
```

### 3. Actualizar Estado del Ticket
Inicia sesión con el correo del **Técnico** (`tecnico@helpflow.com`) para obtener un nuevo token, y úsalo para cambiar el estado del ticket.

*   **Endpoint:** `PUT /api/v1/tickets/{ticketId}/status`
*   **Headers:** `Authorization: Bearer <TOKEN_DEL_TECNICO>`
*   **Body (JSON):**
```json
{
    "targetStatus": "EN_PROGRESS",
    "observation": "Revisando conexiones de video",
    "performedBy": "22222222-2222-2222-2222-222222222222"
}
```

## 🛡️ Manejo Global de Errores

El proyecto implementa el estándar **RFC 7807 (Problem Details for HTTP APIs)**. Cualquier excepción de negocio (ej. transición de estado inválida, técnico no encontrado) o error interno será capturado por el `GlobalExceptionHandler` y devuelto en un formato uniforme y predecible:

```json
{
    "type": "https://helpflow.com/errors/invalid-state",
    "title": "Invalid Ticket State Transition",
    "status": 422,
    "detail": "Cannot transition ticket from OPEN to CLOSED",
    "instance": "/api/v1/tickets/123/status",
    "timestamp": "2026-10-28T00:00:00Z"
}
```