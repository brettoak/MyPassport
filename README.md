# MyPassport - User Center Interface

MyPassport is a comprehensive User Center interface designed to provide robust authentication and user management capabilities. Built with modern Java technologies, it offers a secure and scalable foundation for managing user identities.

## 🚀 Features

-   **User Registration**: Secure sign-up process for new users.
-   **Authentication (Login)**: Robust login mechanism with support for standard authentication flows.
-   **Authorization**: Role-based access control (RBAC) to manage user permissions.
-   **User Profile Management**: Endpoints for users to view and update their profile information.
-   **Scalable Architecture**: Built on Spring Boot for high performance and scalability.

## 🛠 Technology Stack

-   **Java 17**: The latest LTS version of Java for modern language features and performance.
-   **Spring Boot 3.5.10**: A powerful framework for building production-ready applications.
-   **Spring Data JPA**: For easy database interaction and object-relational mapping.
-   **MySQL**: Relational database management system for persistent data storage.


## 🏁 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

-   Java Development Kit (JDK) 17 or higher
-   Maven 3.6+
-   Docker & Docker Compose

### Installation

1.  **Clone the repository**
    ```bash
    git clone https://github.com/yourusername/MyPassport.git
    cd MyPassport
    ```

### Database Setup (Docker)

This project uses Docker Compose to manage MySQL and Redis dependencies.

1.  **Start Services**
    ```bash
    docker compose up -d
    ```
    This will start MySQL on port `3306` (mapped from container) and Redis on port `6379`.

### Running the Application

This project supports four distinct environments:

1.  **Development (`dev`)**: Local development with Docker.
2.  **Test (`test`)**: For automated testing or internal QA.
3.  **UAT/Staging (`uat`)**: Pre-production environment mirroring production.
4.  **Production (`prod`)**: Live production environment.

#### 1. Build the Project
```bash
./mvnw clean package -DskipTests
```

#### 2. Run in Development (Default)
```bash
# Uses local Docker MySQL & Redis configured in application-dev.yaml
java -jar target/MyPassport-0.0.1-SNAPSHOT.jar
# OR
./mvnw spring-boot:run
```

#### 3. Run in Test Environment
```bash
# Connects to test infrastructure
java -jar target/MyPassport-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
```

#### 4. Run in UAT (Pre-Production)
Connects to staging services.

```bash
# Set UAT environment variables
export UAT_DB_URL=jdbc:mysql://uat-db-server:3306/mypassport_uat
export UAT_DB_PASSWORD=uat_secure_password

java -jar target/MyPassport-0.0.1-SNAPSHOT.jar --spring.profiles.active=uat
```

#### 5. Run in Production
Production configuration relies on environment variables for security.

```bash
# Set environment variables for sensitive data
export PROD_DB_URL=jdbc:mysql://prod-db-server:3306/mypassport_prod
export PROD_DB_PASSWORD=your_secure_password

# Run with prod profile
java -jar target/MyPassport-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

The application will start on `http://localhost:8089` (by default).

## 📚 API Documentation

You can view the API documentation and test endpoints using Swagger UI:
[http://localhost:8089/swagger-ui/index.html](http://localhost:8089/swagger-ui/index.html)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
