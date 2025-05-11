## 🛠️ Technology Justification

This microservices system is designed with a focus on **decoupling, scalability, efficiency, and resilience**. Below is an overview of the technologies selected:

### ✅ Spring Boot with WebFlux (Reactive)

Spring Boot provides a robust framework for building Java microservices with minimal configuration and high productivity.
We chose **WebFlux**, Spring’s reactive module, for the following reasons:

* Enables building **non-blocking, highly scalable** services.
* Performs better under high concurrency compared to traditional servlet-based stacks like Spring MVC.
* Naturally aligns with event-driven and reactive architectures, such as Kafka integration.

### ✅ Apache Kafka + Schema Registry + Avro

An **asynchronous event-based architecture** has been adopted to ensure **loose coupling** between services:

* **Kafka** offers a distributed, high-throughput messaging system ideal for handling event streams.
* **Avro** provides efficient and compact binary serialization, well-suited for transmitting structured events.
* **Schema Registry** manages Avro schemas centrally, ensuring schema evolution and compatibility between producers and consumers.

This stack offers schema validation, version control, and performance advantages not typically available with formats like JSON or XML.

### ✅ Redis Cache

Redis is integrated to improve **response times** for frequently accessed data:

* Ideal for caching product listings, price conversions, and available stock.
* Supports atomic operations, TTLs, and high-performance data structures.
* Unlike in-memory or database-level caching, Redis is **distributed**, **persistent**, and production-ready for multi-instance deployments.

### ✅ OpenAPI

OpenAPI (Swagger) is used to:

* Automatically and consistently document REST endpoints.
* Facilitate integration for external consumers and enable code generation.
* Serve as a single source of truth for API contracts, helping identify issues early in development.
