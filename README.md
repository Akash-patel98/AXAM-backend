# AXAM Backend

**AXAM (Online Assessment & Examination Management System)** is a secure and scalable backend application for managing and conducting online examinations.

The backend provides RESTful APIs for authentication, user management, question management, exam creation, blueprints, scheduling, marks configuration, exam attempts, and results.

---

## Overview

AXAM supports two primary roles:

* **ADMIN** — manages the examination system, questions, exams, blueprints, schedules, and marks.
* **CANDIDATE** — views available exams, attempts exams, and accesses their examination results.

The backend is built using **Spring Boot** and follows a layered architecture to provide maintainable, secure, and modular application development.

---

## Key Features

### Authentication & Security

* JWT-based authentication
* Access token and refresh token mechanism
* OAuth2 / Google authentication support
* Email verification
* Forgot password and reset password
* Role-based authorization
* Protected REST APIs using Spring Security
* Login history tracking
* Account status management

### User Management

* Admin and Candidate role management
* User status management
* Candidate profile management
* Account activation/blocking

### Question Bank

* Create, update, and manage questions
* Category-based question management
* Difficulty levels
* Question status management
* Duplicate question detection
* Bulk question import using CSV
* Question filtering and searching

### Exam Management

* Create and manage exams
* Exam status management
* Blueprint-based question selection
* Difficulty-wise question configuration
* Exam duration and marks configuration

### Blueprint Management

* Configure questions according to:

  * Category
  * Difficulty level
  * Question count
* Random question selection based on blueprint configuration

### Exam Scheduling

* Schedule exams for candidates
* Configure start and end time
* Activate/deactivate schedules
* Validate examination availability based on schedule

### Marks Management

* Configure marks according to difficulty level
* Exam-specific marks configuration
* Marks used during evaluation

### Exam Attempts

* Start an examination attempt
* Randomized questions
* Attempt tracking
* Answer submission
* Time tracking
* Automatic submission after time expiry
* Attempt status management
* Attempt session validation
* Attempt statistics

### Results

* Automatic answer evaluation
* Correct and incorrect answer calculation
* Obtained marks
* Percentage calculation
* Attempt summary
* Candidate examination results

---

## Roles & Responsibilities

| Role          | Responsibilities                                                             |
| ------------- | ---------------------------------------------------------------------------- |
| **ADMIN**     | Manage users, categories, questions, exams, blueprints, schedules, and marks |
| **CANDIDATE** | View available exams, attempt exams, view attempts and results               |

---

## Technology Stack

| Technology                      | Purpose                        |
| ------------------------------- | ------------------------------ |
| **Java**                        | Backend programming language   |
| **Spring Boot**                 | Backend application framework  |
| **Spring Security**             | Authentication & authorization |
| **JWT**                         | Token-based authentication     |
| **OAuth2**                      | Social authentication          |
| **Spring Data JPA / Hibernate** | Database persistence           |
| **MySQL**                       | Relational database            |
| **Maven**                       | Dependency management & build  |
| **JavaMailSender**              | Email services                 |
| **Apache Commons CSV**          | CSV question import            |
| **Lombok**                      | Boilerplate code reduction     |

---

## Architecture

AXAM follows a layered backend architecture:

```text
                    Client / Frontend
                           |
                           v
                    REST Controllers
                           |
                           v
                       Services
                           |
                           v
                     Repositories
                           |
                           v
                      Database
```

### Main Layers

**Controller Layer**

Handles HTTP requests and exposes REST APIs.

```text
controller/
├── Admin...
├── Candidate...
├── Auth...
└── ...
```

**Service Layer**

Contains business logic and application rules.

```text
service/
├── impl/
└── ...
```

**Repository Layer**

Handles database operations using Spring Data JPA.

```text
repository/
└── ...
```

**DTO Layer**

Used for request and response data transfer.

```text
dto/
├── request/
└── responce/
```

**Entity Layer**

Contains database entities and their relationships.

```text
entity/
└── ...
```

---

## Project Structure

```text
src/
└── main/
    ├── java/
    │   └── com/arishi/AXAM/
    │       ├── config/
    │       ├── controller/
    │       ├── dto/
    │       │   ├── request/
    │       │   └── responce/
    │       ├── entity/
    │       ├── enums/
    │       ├── exception/
    │       ├── repository/
    │       ├── security/
    │       ├── service/
    │       │   └── impl/
    │       ├── util/
    │       └── AxamApplication.java
    │
    └── resources/
        ├── application.properties
        └── ...
```

---

## API Base URL

All application APIs use the following base path:

```text
/api/v1
```

Example:

```text
/api/v1/auth
/api/v1/users
/api/v1/questions
/api/v1/exams
/api/v1/marks
/api/v1/candidate
```

---

## Authentication Flow

AXAM uses JWT-based authentication.

```text
User
  |
  | Login
  v
Authentication API
  |
  | Validate credentials
  v
Generate Access Token
  +
Generate Refresh Token
  |
  v
Client
```

The access token contains user-related claims such as:

```text
userId
role
email
```

Protected APIs validate the JWT before allowing access.

---

## Exam Flow

The examination process follows this general flow:

```text
Create Exam
     |
     v
Configure Blueprint
     |
     v
Configure Marks
     |
     v
Create Schedule
     |
     v
Activate Exam
     |
     v
Candidate Views Exam
     |
     v
Candidate Starts Attempt
     |
     v
Questions Assigned Randomly
     |
     v
Candidate Answers Questions
     |
     v
Submit / Auto Submit
     |
     v
Evaluate Attempt
     |
     v
Generate Result
```

---

## Exam Attempt Status

An exam attempt can have the following statuses:

```text
IN_PROGRESS
SUBMITTED
AUTO_SUBMITTED
```

### IN_PROGRESS

The candidate has started the examination and the attempt is active.

### SUBMITTED

The candidate has manually submitted the examination.

### AUTO_SUBMITTED

The examination is automatically submitted when the allowed examination time expires.

---

## Question Selection

Questions are selected according to the configured blueprint.

A blueprint can define:

```text
Category
Difficulty Level
Question Count
```

Example:

```text
Java
 ├── EASY   → 5 Questions
 ├── MEDIUM → 3 Questions
 └── HARD   → 2 Questions
```

The backend selects questions dynamically and randomizes the question pool before assigning questions to the candidate.

---

## Email Services

The backend supports email-based functionality such as:

* Email verification
* Password reset
* Authentication-related notifications

Verification and reset links contain secure tokens with limited validity.

---

## Configuration

Create your local configuration in:

```text
src/main/resources/application.properties
```

Typical configuration includes:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/axam
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update

jwt.secret=YOUR_SECRET_KEY

frontend.url=YOUR_FRONTEND_URL

spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_EMAIL_PASSWORD
```

> **Important:** Never commit real passwords, JWT secrets, API keys, or email credentials to Git.

For production, sensitive configuration should be provided through environment variables or a secure secrets manager.

---

## Prerequisites

Before running the backend, make sure the following are installed:

* Java JDK
* Maven
* MySQL
* Git

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

---

## Database Setup

Create the AXAM database in MySQL:

```sql
CREATE DATABASE axam;
```

Then configure the database credentials in:

```text
application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/axam
spring.datasource.username=root
spring.datasource.password=your_password
```

Hibernate/JPA will manage the required database tables according to the configured application settings.

---

## Running the Application

### 1. Clone the repository

```bash
git clone <repository-url>
```

### 2. Navigate to the backend

```bash
cd AXAM-backend
```

### 3. Build the project

```bash
mvn clean install
```

### 4. Run the application

```bash
mvn spring-boot:run
```

The backend will start on the configured Spring Boot port.

Default Spring Boot port:

```text
8080
```

---

## Build JAR

To create a production JAR:

```bash
mvn clean package
```

Run the generated JAR:

```bash
java -jar target/<application-name>.jar
```

---

## Security

AXAM uses several security mechanisms:

* JWT authentication
* Refresh token authentication
* Password hashing
* Role-based authorization
* Protected REST endpoints
* Email verification
* Password reset token validation
* Account status validation
* Exam attempt ownership validation
* Active session validation

Example role protection:

```java
@PreAuthorize("hasRole('ADMIN')")
```

Candidate-specific APIs:

```java
@PreAuthorize("hasRole('CANDIDATE')")
```

---

## Error Handling

The backend uses centralized exception handling to provide consistent API responses for common errors such as:

* Validation errors
* Unauthorized access
* Resource not found
* Duplicate resources
* Invalid tokens
* Expired tokens
* Invalid exam attempts

---

## API Documentation

API documentation can be added using **Swagger / OpenAPI** for interactive API testing and endpoint documentation.

Once configured, the documentation can be accessed through the application's Swagger endpoint.

---

## Development Guidelines

When adding a new feature:

```text
Request DTO
    ↓
Controller
    ↓
Service Interface
    ↓
Service Implementation
    ↓
Repository
    ↓
Entity / Database
    ↓
Response DTO
```

Recommended practices:

* Keep business logic inside services.
* Keep controllers focused on HTTP handling.
* Use DTOs instead of exposing entities directly.
* Validate incoming requests.
* Use meaningful exception messages.
* Protect role-specific endpoints.
* Avoid storing sensitive credentials in source control.
* Keep database operations inside repositories.

---

## Main Modules

```text
Authentication
      │
      ├── Login
      ├── Signup
      ├── JWT
      ├── Refresh Token
      ├── OAuth2
      ├── Email Verification
      └── Password Reset

User Management
      │
      ├── Admin
      └── Candidate

Question Management
      │
      ├── Categories
      ├── Questions
      ├── Difficulty
      └── CSV Import

Exam Management
      │
      ├── Exams
      ├── Blueprints
      ├── Marks
      └── Schedulers

Candidate Examination
      │
      ├── Available Exams
      ├── Exam Attempts
      ├── Answers
      ├── Auto Submission
      └── Results
```

---

## Future Enhancements

Potential improvements include:

* Swagger/OpenAPI documentation
* Automated testing and integration tests
* Advanced examination analytics
* Improved audit logging
* Production monitoring and observability
* Containerized deployment
* CI/CD pipeline

---

## License

This project is developed as part of the **AXAM Online Assessment Platform**.

---

## Author

**Akash Patel**

> AXAM — Secure. Structured. Reliable Online Assessment.
