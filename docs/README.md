# 🎫 Ticket Lifecycle, Assignment Flow & SLA Rules

## 1. Ticket Status Lifecycle

Tickets follow a strict lifecycle validated using Bean Validation and cross-field validation rules.

```text
OPEN → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED
```

Tickets can also transition to `CANCELLED` when cancellation rules permit it.

### Automatic Assignment and SLA Flow

1. **Ticket Creation:** A user creates a ticket, which starts in the `OPEN` state.
2. **SLA Calculation:** The system automatically calculates the SLA deadline based on the ticket priority: `LOW`, `MEDIUM`, `HIGH`, or `CRITICAL`.
3. **Dispatch Integration:** The `support-ticket-service` calls the `technician-dispatch-service` through a REST adapter.
4. **Assignment Logic:** The external dispatch service uses a deterministic algorithm based on `ticketId`, category, and priority to suggest a technician and estimate resolution time according to availability (`HIGH`, `MEDIUM`, or `LOW`).
5. **Transactional Assignment:** The core service applies internal business rules, including maximum active tickets per technician and specialty matching. It then registers the assignment and creates an assignment history record within a single `@Transactional` operation.
6. **SLA Escalation:** Tickets approaching an SLA deadline are automatically escalated according to the system's escalation rules.

---

## 🚀 2. API Endpoints Reference

Refer to the included Postman collection and Swagger UI for detailed request and response payloads.

### Authentication Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/register` | Register a new user or technician. |
| `POST` | `/auth/login` | Authenticate and retrieve a JWT token. |

### Support Ticket Service (Core)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/tickets` | Create a new ticket. |
| `GET` | `/api/v1/tickets/{id}` | Retrieve ticket details. |
| `PUT` | `/api/v1/tickets/{id}/status` | Update ticket status and record history. |
| `GET` | `/actuator/health` | Check system health. |
| `GET` | `/actuator/metrics` | Retrieve application and system metrics. |

**Access control:**
- Ticket creation is available to authorized users.
- Ticket status updates are restricted to technicians and administrators.
- Protected endpoints require valid authentication and authorization.

### Technician Dispatch Service (External Mock)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/technician-dispatch` | Returns a deterministic technician assignment suggestion and availability information. |

Expected request fields:

```json
{
  "ticketId": "example-ticket-id",
  "categoria": "HARDWARE",
  "prioridad": "HIGH"
}
```

> Note: The example values are illustrative. Use the field types and accepted values defined by the actual API implementation.

---

## 🛠️ 3. Local Execution & Deployment

### Prerequisites

Ensure the following tools are installed:

- JDK 17 or later
- Docker and Docker Compose
- Maven (if running Maven commands directly)

### Option 1: Run with Docker Compose (Recommended)

Docker Compose starts the database, core service, and technician dispatch service in isolated containers using multi-stage Dockerfiles.

**1. Ensure Docker is running.**

**2. Open a terminal in the project root directory.**

**3. Build and start the services:**

```bash
docker-compose up --build -d
```

**4. Access the services:**

| Service | URL |
|---|---|
| Support Ticket Service | [http://localhost:8080](http://localhost:8080) |
| Technician Dispatch Service | [http://localhost:8081](http://localhost:8081) |

### Option 2: Run Locally for Development

**1. Start the PostgreSQL database:**

From the project root directory, run:

```bash
docker-compose up -d db
```

**2. Start the Technician Dispatch Service:**

```bash
cd technician-dispatch-service
./mvnw spring-boot:run
```

**3. Start the Support Ticket Service:**

In a separate terminal:

```bash
cd support-ticket-service
./mvnw spring-boot:run
```

> Ensure each service has the correct database connection settings, environment variables, and port configuration before starting it.

---

## 🧪 4. Testing & Observability

### Automated Tests

The project includes an automated test suite using JUnit 5, Mockito, and Testcontainers to validate application behavior and integration scenarios.

Test coverage includes:

- **Unit Tests:** Domain logic, SLA calculations, and mocked hexagonal architecture ports.
- **Integration Tests:** JPA repositories, REST adapters, JWT authentication and authorization flows, and database interactions using Testcontainers.

Run the tests with Maven:

```bash
mvn clean test
```

> Run this command from the appropriate Maven project directory, or from the root if the project is configured as a multi-module Maven build.

### Metrics and Logging

Spring Boot Actuator and Micrometer are configured to monitor application behavior, including:

- Mean time to first response.
- Mean time to ticket resolution.
- Tickets that have breached their SLA.
- Ticket creation grouped by category and priority.
- Authentication failures and denied access attempts.

### Global Exception Handling

Global exception handling is implemented using `@ControllerAdvice`.

API errors are standardized using RFC 9457 Problem Details, covering scenarios such as:

- JPA and database-related errors.
- Request validation failures.
- Authentication and authorization errors.
- Access-denied exceptions.

---

## 📸 5. Evidence & Screenshots

Add screenshots demonstrating the application functionality and test results.

### API Testing

_Insert Postman requests and Swagger UI screenshots here._

### SLA Monitoring and Metrics

_Insert screenshots showing SLA calculations, escalations, SLA breaches, and Actuator metrics here._

### Structured Logging

_Insert screenshots demonstrating structured application logs and error handling here._

---

## 📚 6. Technology Stack

- **Language:** Java 17+
- **Framework:** Spring Boot
- **Security:** Spring Security and JWT
- **Persistence:** Spring Data JPA and PostgreSQL
- **Architecture:** Hexagonal architecture
- **Validation:** Bean Validation
- **Inter-service Communication:** REST
- **Testing:** JUnit 5, Mockito, and Testcontainers
- **Observability:** Spring Boot Actuator and Micrometer
- **Containerization:** Docker and Docker Compose
- **Build Tool:** Maven
