<img src="SpringSecurity.png" alt="Spring Security">

## Application Security

The application's security is implemented using **Spring Security** with a role-based and dynamically configured path-matching approach. Below is a detailed explanation of the implementation:

 ### 1. Security Configuration
The `SecurityConfig` class defines access rules using `SecurityWebFilterChain`. These rules are dynamically configured from a configuration file (`application.yml`), where paths, HTTP methods, and allowed roles are specified. Key steps include:

- **CSRF Disabled**: To simplify token management in reactive applications.
- **Dynamic Authorization**: Security rules are read from the `application.yml` file and applied using `ServerWebExchangeMatchers`, which validate HTTP methods and paths for each request.
- **Password Encoding**: `BCryptPasswordEncoder` is used to ensure secure password storage.

### 2. Role-Based Path Matching
In the `application.yml` file, paths, HTTP methods, and access roles are defined. A typical example looks like this:

```yaml
app:
  security:
    roles:
    - USER
    - ADMIN
    rules:
      - path: /api/v1/stock
        method: GET
        roles: [ USER, ADMIN ]
      - path: /api/v1/stock
        method: POST
        roles: [ ADMIN ]
```

These configurations are processed by the `SecurityConfig` class, which applies the rules using `pathMatchers` and `methodMatchers`.

### 3. JWT Token Generation and Validation
The `JwtService` class handles the generation and validation of JWT tokens for user authentication. Key features include:

- **Token Generation**: The `generateToken` method creates a token signed with a secret key, including the username and roles in the claims.
- **Token Validation**: The `parseToken` method validates the token and extracts information from the claims.
- **Secure Keys**: The `io.jsonwebtoken.security.Keys` library is used to securely handle the secret key.

### 4. Utility Methods
- **passwordMatches**: Implemented in `JwtService`, this method verifies if a raw password matches an encoded password stored in the database using `PasswordEncoder`.

### 5. Security Workflow
1. When a user makes a request, Spring Security checks the rules defined in `application.yml` to determine access.
2. If the user has a valid JWT token, they are authenticated and allowed to access secured paths.
3. User passwords are securely validated and stored using bcrypt encoding.

### 6. Benefits
- **Flexibility**: Security rules can be modified dynamically via the configuration file without changing the code.
- **Scalability**: The implementation is suitable for both small applications and larger systems requiring multiple roles and secured paths.
- **Security**: Modern practices, such as password encoding and JWT tokens signed with secure keys, are used.

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
2. Set the URL to `http://localhost:8080/api/v1/auth/login`.
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
