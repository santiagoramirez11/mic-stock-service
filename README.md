# Stock Service

Stock management microservice in the stock system. Built with Spring Boot + WebFlux, JWT security, Kafka + Avro, MongoDB, and Redis.

---

## 🔄 Technologies

- Java 21
- Spring Boot 3
- Spring WebFlux
- Spring Security (JWT)
- Kafka + Avro + Schema Registry
- MongoDB Reactive
- Redis Reactive
- Docker Compose
- Testcontainers for testing
- OpenAPI 3 (Swagger UI)
- MapStruct

---

## 📚 Project Architecture
<img src="docs/architecture/ArchitectureComponent.png" alt="Component">

---

## 🚀 Key Features

- Adjust, retrieve, and delete product stock.
- Listen to product creation/deletion events from Kafka.
- Emit stock adjustment events.
- Cache stock in Redis for fast queries.
- OpenAPI documentation.
- JWT Security: `ADMIN` and `USER` roles.

---

## 📚 Project Flow
<img src="docs/architecture/Flow.png" alt="Flow">

---

## 🐘 Kafka Topics

| Event                            | Kafka Topic                        |
|----------------------------------|------------------------------------|
| Product created (listened)       | `product-created-v1`               |
| Product deleted (listened)       | `product-deleted-v1`               |
| Stock adjusted (emitted)         | `product-stock-adjusted-v1` |

---
## 🔗 Key REST Endpoints

| Method | URL                                    | Description                      |
|--------|----------------------------------------|----------------------------------|
| POST   | `/api/v1/auth/login`                   | Login to the system              |
| POST   | `/api/v1/auth/register`                | Register a new user              |
| POST   | `/api/v1/stock/adjust`                 | Adjust stock of a product        |
| GET    | `/api/v1/stock`                        | List the complete stock          |
| GET    | `/api/v1/stock/{productId}`            | Retrieve stock of a product      |
| DELETE | `/api/v1/stock/{productId}`            | Delete stock of a product        |
   
---

## 📆 Installation Requirements

- Docker and Docker Compose installed
- Java 21
- Maven 3.9+

---

## 🛠️ Local Setup

1. Clone the Repository
```bash
git clone https://github.com/santiagoramirez11/mic-stock-service.git
cd mic-productservice
```

2. Build the Project
```bash
mvn clean install
```

3. Start the infrastructure:

```bash
docker-compose up -d
```

4Run the microservice:

```bash
mvn spring-boot:run
```

The application will be available at http://localhost:9090

💡 Key variables in `application.yml`:
- MongoDB URI: `mongodb://mongo:27017/product`
- Kafka Bootstrap Servers: `kafka:9092`
- Schema Registry URL: `http://schema-registry:8081`
- Redis Host: `redis`

🧪 Testing
- Unit tests with JUnit 5.
- Kafka and MongoDB integration tests using Testcontainers.