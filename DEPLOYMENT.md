# RED-LINK Deployment Guide

This guide details how to package and run the **Red-link** application on a server environment.

## 1. Prerequisites
Before deployment, ensure the server has the following installed:
*   **Java Development Kit (JDK) 21**: Required to run the application.
*   **MySQL Server 8.0+**: Database engine.
*   **Maven**: (Optional) For building, though the wrapper `./mvnw` is included.

## 2. Configuration
Ensure your `src/main/resources/application.properties` is configured for the production database:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/blood_link?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

# Server Configuration
server.port=8080
```

> **Note**: For production, consider using environment variables for passwords instead of hardcoding them.

## 3. Building the Application
To create a strictly executable JAR file, run the following command in the project root:

#### Windows (Command Prompt/PowerShell)
```powershell
./mvnw clean package -DskipTests
```

#### Linux/Mac
```bash
./mvnw clean package -DskipTests
```

This will generate a JAR file in the `target/` directory, typically named `blood-management-system-0.0.1-SNAPSHOT.jar`.

## 4. Running on Server
Once the build is complete, you can run the application directly:

```bash
java -jar target/blood-management-system-0.0.1-SNAPSHOT.jar
```

### Running in Background (Linux)
To keep the server running even after you disconnect:

```bash
nohup java -jar target/blood-management-system-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

## 5. Accessing the Application
*   **Local Access**: Open `http://localhost:8080`
*   **Network Access**: Open `http://<SERVER_IP>:8080` (Ensure firewall allows port 8080).

## 6. Troubleshooting
*   **Port in use**: If port 8080 is busy, change `server.port` in `application.properties` or run with:
    ```bash
    java -Dserver.port=8081 -jar target/blood-management-system-0.0.1-SNAPSHOT.jar
    ```
*   **Database Connection**: Ensure MySQL service is running and credentials are correct.
