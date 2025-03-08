# **Fantasy Game API - Spring Boot & Kotlin**

## **Project Overview**
A **RESTful API** built with **Spring Boot** and **Kotlin** that simulates a fantasy game where players can create characters and engage in battles. This project showcases **clean architecture, API design, and backend development skills**.

## **Features**

### **Character Management**
- Create **warriors** or **sorcerers** with unique attributes.

### **Battle System**
- Simulate **battles** between characters with round-by-round combat.

### **Leaderboards**
- Track **player performance and rankings**.

### **Authentication**
- Secure API endpoints with **Basic Authentication**.

---

## **Technical Highlights**

### **Spring Boot**
- Leveraging **Spring's powerful ecosystem** for a robust application.

### **Kotlin**
- Using **Kotlin's expressive syntax** and **null safety features**.

### **Layered Architecture**

- **Controller Layer**: Handles HTTP requests and responses.
- **Service Layer**: Implements **business logic**.
- **Repository Layer**: Manages **data persistence**.
- **DTOs**: Separate **data transfer objects** for different layers.

### **RESTful API Design**
- Well-structured **endpoints following REST principles**.

### **Testing**
- Comprehensive **unit tests** with **JUnit and Mockk**.

---

## **Project Structure**
```
Copyfantasy-space-game/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com.motycka.edu/
│   │   │       └── game/
│   │   │           ├── account/       # User authentication and management
│   │   │           ├── character/     # Character creation and management
│   │   │           ├── config/        # Application configuration
│   │   │           ├── error/         # Error handling
│   │   │           ├── leaderboard/   # Leaderboard functionality
│   │   │           ├── match/         # Battle simulation
│   │   │           └── GameApplication.kt
│   │   └── resources/
│   │       ├── static/               # Static resources for UI
│   │       ├── application.yaml      # Application configuration
│   │       ├── data.sql              # Initial data
│   │       ├── schema.sql            # Database schema
│   │       └── logback-spring.xml    # Logging configuration
│   └── test/                         # Test cases
└── build.gradle.kts                  # Gradle build configuration
```

---

## **API Endpoints**

### **Characters API**
- **GET** `/api/characters` - Retrieve **all characters** with optional filters.
- **GET** `/api/characters/{id}` - Retrieve **a specific character**.
- **POST** `/api/characters` - **Create** a new character.
- **PUT** `/api/characters/{id}` - **Update** a character (level up).
- **GET** `/api/characters/challengers` - Get **characters owned by the current user**.
- **GET** `/api/characters/opponents` - Get **characters not owned by the current user**.

### **Matches API**
- **GET** `/api/matches` - Retrieve **all matches**.
- **POST** `/api/matches` - **Create** a new match between characters.

### **Leaderboard API**
- **GET** `/api/leaderboards` - Get **ranked leaderboard** with optional class filter.

### **User Management API**
- **POST** `/api/accounts` - **Register** a new user.

---

## **Technical Implementation**

### **Database**
- **H2 database** for **development and testing**.

### **Authentication**
- **Basic Auth implementation**.

### **Error Handling**
- Comprehensive **error handling** with appropriate **HTTP status codes**.

### **Validation**
- **Input validation** for all endpoints.

### **Testing**
- **Unit tests** for all layers of the application.

---

## **How to Run**

1. **Clone the repository**.
2. **Run** `./gradlew bootRun`.
3. **Access the UI** at [http://localhost:8090/](http://localhost:8090/).
4. **Register a user** and start creating characters!

---
