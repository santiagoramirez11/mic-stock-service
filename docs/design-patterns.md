### Design Patterns Applied

The following design patterns are applied:

1. **Service Layer Pattern**:
    - Classes like `StockServiceImpl` and `StockMovementServiceImpl` implement business logic and act as a service layer, separating the business logic from the controllers and repositories.

2. **Repository Pattern**:
    - The use of `StockRepository` and `StockMovementRepository` abstracts database operations, providing a clean interface for data access.

3. **Dependency Injection**:
    - Spring's `@Autowired` and `@InjectMocks` annotations are used to inject dependencies, promoting loose coupling between components.

4. **Builder Pattern**:
    - The `Stock` and `StockMovement` classes use Lombok's `@Builder` annotation to implement the builder pattern, simplifying object creation.

5. **Observer Pattern**:
    - The `ProductEventListener` listens to Kafka events (`ProductCreatedEventV1`, `ProductDeletedEventV1`), reacting to changes in the system.

6. **DTO (Data Transfer Object) Pattern**:
    - Classes like `AdjustStockRequestDto`, `StockResponseDto`, and `MovementHistoryItemDto` are used to transfer data between layers, ensuring separation of concerns.

7. **Mapper Pattern**:
    - The `StockDtoMapper` is used to map between domain models (`Stock`, `StockMovement`) and DTOs, simplifying data transformation.

8. **Reactive Programming**:
    - The use of `Mono` and `Flux` from Project Reactor follows a reactive design pattern, enabling asynchronous and non-blocking operations.

9. **Factory Pattern**:
    - The `openapi-generator-maven-plugin` generates API models and controllers, acting as a factory for creating boilerplate code.

10. **Strategy Pattern** (implicit):
    - The `MovementType` enum (`IN`, `OUT`) can be seen as a strategy for handling different stock adjustment behaviors.

11. **Proxy Pattern**:
    - Kafka and Redis act as intermediaries (proxies) for communication and caching, abstracting the underlying complexity.

12. **Template Method Pattern**:
    - The `spring-boot-starter-test` and `reactor-test` libraries provide predefined testing templates, allowing developers to focus on test logic.

These patterns collectively ensure modularity, scalability, and maintainability in the project.

### Design Principles Applied

1. **Single Responsibility Principle (SRP)**:
    - Each class has a single responsibility, e.g., `StockServiceImpl` handles stock-related business logic, while `StockRepository` manages database operations.

2. **Open/Closed Principle (OCP)**:
    - The system is open for extension but closed for modification, e.g., adding new Kafka events or stock adjustment types without altering existing code.

3. **Dependency Inversion Principle (DIP)**:
    - High-level modules depend on abstractions (e.g., `StockService` interface), not concrete implementations.

4. **Interface Segregation Principle (ISP)**:
    - Interfaces are specific to client needs, e.g., `StockService` and `StockMovementService` define distinct responsibilities.

5. **Separation of Concerns (SoC)**:
    - Layers like controllers, services, and repositories ensure modularity and maintainability.

6. **DRY (Don't Repeat Yourself)**:
    - Common logic is centralized, e.g., mapping logic in `StockDtoMapper`.

7. **KISS (Keep It Simple, Stupid)**:
    - The architecture avoids unnecessary complexity, focusing on clear and concise solutions.

8. **SOLID Principles**:
    - The project adheres to all SOLID principles, ensuring scalability and maintainability.

These patterns and principles ensure the project is modular, scalable, and easy to maintain.