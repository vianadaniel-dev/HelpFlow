# HelpFlow - IT Support Ticketing System

HelpFlow is a robust, microservices-ready IT Support Ticketing System built with Java 21 and Spring Boot 3. It manages users, technicians, and support tickets, handling automatic technician dispatching and enforcing strict SLA (Service Level Agreement) business rules.

## 🚀 Tech Stack
* **Language:** Java 21
* **Framework:** Spring Boot 3.3.4
* **Database:** PostgreSQL 17
* **Migrations:** Flyway
* **Security:** JJWT (Custom JWT Implementation)
* **Mapping:** MapStruct & Lombok
* **Containerization:** Docker & Docker Compose
* **Architecture:** Hexagonal Architecture (Ports and Adapters)

## 🏗️ Hexagonal Architecture (Ports & Adapters)
The project strictly follows Hexagonal Architecture principles to decouple business logic from infrastructure concerns.

1. **Domain Layer (`domain`):** Contains the core business logic, entities (`User`, `Technician`, `Ticket`), and exceptions. It has **zero dependencies** on Spring or external libraries.
2. **Application Layer (`application`):** Orchestrates use cases (`RegisterTicketUseCase`, `ManageTicketUseCase`). It defines **Ports** (interfaces) to communicate with the outside world.
3. **Infrastructure Layer (`infrastructure`):** Implements the ports. 
   * **Inbound Adapters (`in`):** REST Controllers (`TicketController`, `AuthController`) receiving HTTP requests.
   * **Outbound Adapters (`out`):** Persistence (JPA/Hibernate), external REST Clients (`TechnicianDispatchRestAdapter`), and Security configurations.

## 🛠️ Prerequisites
* **Java 21** installed.
* **Maven** (or use the included `./mvnw` wrapper).
* **Docker & Docker Compose** for running the database.

## 🐳 Running the Application

1. **Start the PostgreSQL Database:**
   ```bash
   docker-compose up -d
   ```
   *This starts Postgres on port 5433 to avoid local conflicts. The `V1` and `V2` Flyway scripts will automatically create the schema and insert seed data.*

2. **Run the Spring Boot Application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   *The API will be available at `http://localhost:8080`.*

## 🧪 Testing the API (Postman / cURL)

**1. Authenticate (Get JWT Token)**
```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
    "corporateEmail": "daniel@helpflow.com",
    "password": "123456"
}
```
*Extract the `accessToken` from the response.*

**2. Create a Ticket**
```http
POST http://localhost:8080/api/v1/tickets
Authorization: Bearer <YOUR_TOKEN>
Content-Type: application/json

{
    "requesterId": "11111111-1111-1111-1111-111111111111",
    "title": "My PC won't turn on",
    "description": "Blue screen on Windows startup",
    "category": "HARDWARE",
    "headquarters": "BARRANQUILLA"
}
```

## 🛡️ Error Handling
This project implements the RFC 7807 standard (Problem Details for HTTP APIs). Any business exceptions (e.g., invalid state transitions, technician not found) or internal errors will be caught by the `GlobalExceptionHandler` and returned in a predictable, uniform format:
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