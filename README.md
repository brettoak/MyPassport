# MyPassport - User Center Interface

MyPassport is a comprehensive User Center interface designed to provide robust authentication, authorization, and user management capabilities. Built with modern Java technologies, it offers a secure and scalable foundation for managing user identities, roles, and permissions.

## 🚀 Features

- **User Registration & Email Verification**: Secure sign-up process with email verification code support (via JavaMailSender).
- **Authentication (Login)**: Robust login mechanism using Spring Security and RSA-signed JWT.
- **Advanced Token Management**: JWT-based access tokens with refresh token support, plus Redis-backed token revocation (Logout / Logout All Devices / Kick Specific Device).
- **Multi-System RBAC (Role-Based Access Control)**: 
  - Centralized permission management for multiple downstream systems (e.g., `passport`, `sys-b`).
  - Scoped roles and permissions using `sysCode` markers.
  - Dedicated `/check-permission` endpoint supporting exact string matches, wildcard paths (`/api/v1/orders/**`), and method+path combos (`GET:/api/v1/users`).
- **Dynamic Profile Delivery**: The user profile payload groups roles and permissions by their respective systems natively (`Map<String, Set<String>>`).
- **Password Management**: Forgot password and secure password reset workflows.
- **API Documentation**: Built-in Swagger UI powered by Springdoc OpenAPI.
- **Database Migration**: Automated schema management using Flyway.

## 🛠 Technology Stack

- **Java 17**: The latest LTS version of Java for modern language features and performance.
- **Spring Boot 3.5.10**: A powerful framework for building production-ready applications.
- **Spring Data JPA & Hibernate**: For easy database interaction and object-relational mapping.
- **MySQL**: Relational database management system for persistent data storage.
- **Redis**: Used for high-performance caching and JWT token management (revocation rules, refresh tokens).
- **Spring Security & JJWT (0.11.5)**: For securing endpoints and handling JSON Web Tokens.
- **Flyway**: For reliable database migrations.
- **Datafaker**: For test data generation.

## 🏁 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven 3.6+
- Docker & Docker Compose

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/MyPassport.git
   cd MyPassport
   ```

### Database Setup (Docker)

This project uses Docker Compose to manage MySQL and Redis dependencies.

1. **Start Services**
   ```bash
   docker compose up -d
   ```
   This will start MySQL and Redis on standard development ports. By default, `application-dev.yaml` references MySQL on port `3308` and Redis on `6379` natively.

### Running the Application

This project supports four distinct environments configured via `application-{profile}.yaml`:

1. **Development (`dev`)**: Local development.
2. **Test (`test`)**: For automated testing or internal QA.
3. **UAT/Staging (`uat`)**: Pre-production environment mirroring production.
4. **Production (`prod`)**: Live production environment.

#### 1. Build the Project
```bash
./mvnw clean package -DskipTests
```

#### 2. Run in Development (Default)
The application uses the `dev` profile by default. Ensure your local Docker MySQL and Redis are running.
```bash
java -jar target/MyPassport-0.0.1-SNAPSHOT.jar
# OR
./mvnw spring-boot:run
```

#### 3. Run in other environments
To run in `test`, `uat`, or `prod`, specify the active profile and inject the required database credentials via environment variables:

```bash
# Example for Production
export PROD_DB_URL=jdbc:mysql://prod-db-server:3306/mypassport_prod
export PROD_DB_PASSWORD=your_secure_password

java -jar target/MyPassport-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

By default, the application will start on `http://localhost:8089`.

## 🚢 Deployment & Environment Migration

If you are changing the deployment server or migrating to a new environment, please ensure you update the following configurations to maintain continuous integration and application stability.

### 1. GitHub Actions Secrets
The CI/CD pipeline located in `.github/workflows/deploy.yml` relies on GitHub Action Secrets to securely connect to your server and inject production variables. If your server changes, you must go to your repository's **Settings -> Secrets and variables -> Actions** and update the following:

- **Server Authentication**: 
  - `SERVER_HOST` (The new server's IP address)
  - `SERVER_USER` (The new server's SSH username)
  - `SERVER_SSH_KEY` (The new server's private SSH key for passwordless login)
- **Database Connection**: 
  - `DB_URL` (e.g., `jdbc:mysql://new-db-host:3306/mypassport_prod`)
  - `DB_USERNAME`
  - `DB_PASSWORD`
- **Other Infrastructure**: Any `PROD_REDIS_*` or `PROD_MAIL_*` variables if those services are also being migrated.

### 2. MySQL Connection Security
When moving to a new server, the database's remote access permissions must be updated:
- Ensure the new application server's IP address is whitelisted in your MySQL server's **Security Groups** or firewall (e.g., AWS Security Groups, UFW, or iptables).
- Ensure the MySQL database user (`DB_USERNAME`) has the correct host privileges (e.g., `user@'new-app-server-ip'`) to connect remotely.

## 📚 API Documentation

Once the application is running, you can view the API documentation and test endpoints using Swagger UI:
[http://localhost:8089/swagger-ui/index.html](http://localhost:8089/swagger-ui/index.html)

### Core API Modules
- **/api/v1/auth**: Registration, Login, Token Refresh, Password Reset, Check Token, Logout. 
  - Includes the powerful `POST /check-permission` validation endpoint for external systems.
- **/api/v1/users**: User profile & device management.
  - Features a multi-system profile representation where roles and permissions are partitioned by `sysCode`.
- **/api/v1/roles**: Role creation and assignment. Supports `?sysCode=` query filters for system isolation.
- **/api/v1/permissions**: Permission management for RBAC. Supports `?sysCode=` query filters for system isolation.
- **/api/v1/system**: System-level configurations or health checks.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
