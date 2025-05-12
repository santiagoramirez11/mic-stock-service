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

See more in the **[Technology Justification Page](docs/technology.md)**

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

## 🔄 Design Patterns and Principles Applied

- Service Layer Pattern.
- Repository Pattern.
- Builder Pattern.
- Observer Pattern.
- DTO (Data Transfer Object) Pattern.
- Mapper Pattern.
- Reactive Programming.
- Singleton Pattern.
- Factory Pattern.
- Strategy Pattern.
- Proxy Pattern.
- Template Method Pattern.

See more in the **[Design Patterns and Principles Page](docs/design-patterns.md)**

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
- MongoDB URI: `mongodb://mongo:27017/stock`
- Kafka Bootstrap Servers: `kafka:9092`
- Schema Registry URL: `http://schema-registry:8081`
- Redis Host: `redis`

🧪 Testing
- Unit tests with JUnit 5.
- Kafka and MongoDB integration tests using Testcontainers.

---

## 🔐 Instructions for Login

The application provides an authentication mechanism using a REST endpoint for login. Below are the details and steps to perform login:

### 1. Endpoint Information

- **Endpoint:** `api/v1/auth/login`
- **Method:** `POST`
- **Content-Type:** `application/json`

This endpoint accepts user credentials (username and password) and returns a JWT token upon successful authentication.

### 2. Default Admin Credentials

The application comes with a default admin user configured in the `application.yml` file. These credentials can be modified as per your requirements.

- **Default Username:** `admin`
- **Default Password:** `admin123`

### 3. Example Request

To log in, send a `POST` request to the login endpoint with the following JSON payload:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

### 4. Example Response

Upon successful authentication, the server will return a JSON response containing the JWT token. Below is an example response:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbi1hY3R1YXRvciIsInJvbGVzIjoiUk9MRV9BQ1RVQVRPUiIsImlhdCI6MTc0Njk4Mjk4NywiZXhwIjoxNzQ2OTg2NTg3fQ.IUrsEaFngmjrRKenxNR5hp7KVhK6P8LJi90WakEXl-U",
  "tokenType": "Bearer",
  "expiresIn": 3600000
}
```

#### Example Using Postman:
1. Open Postman and create a new `POST` request.
2. Set the URL to `http://localhost:9090/api/v1/auth/login`.
3. Go to the "Body" tab and select "raw".
4. Set the body type to `JSON` and paste the following payload:
   ```json
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
5. Send the request, and you should receive the JWT token in the response.

### 5. Modifying Default Credentials

To modify the default credentials, edit the `application.yml` file and update the values under the appropriate section. For example:

```yaml
app:
  security:
    admin:
      username: admin
      password: admin123
```

After modifying the credentials, restart the application to apply the changes.

### 6. Token Usage

The returned JWT token can be used to authenticate subsequent requests by including it in the `Authorization` header as follows:

```
Authorization: Bearer <your-jwt-token>
```

For example:

```bash
curl -X GET http://localhost:9090/api/v1/stock \
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

See more about security applied in the **[Security Page](docs/security/security.md)**