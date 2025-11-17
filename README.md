# Hotel Reservation System (HRS)

A comprehensive web-based hotel and restaurant management system built with Spring Boot, featuring table management, reservations, staff management, task tracking, and customer ratings.

![HMS Logo](src/main/resources/static/images/hms-logo.png)

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Project Structure](#-project-structure)
- [Usage](#-usage)
- [API Endpoints](#-api-endpoints)
- [Screenshots](#-screenshots)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### 🏨 Core Features

- **Reservation Management**
  - Create, view, update, and cancel reservations
  - Real-time table availability tracking
  - Reservation status management (Pending, Confirmed, Seated, In Service, Completed, Cancelled)
  - Customer information management
  - Reservation filtering and search

- **Table Management**
  - Restaurant table configuration (capacity, status)
  - Real-time table status tracking (Available, Reserved, Occupied)
  - Automatic table status updates based on reservations
  - Table assignment to staff members

- **Staff Management**
  - Complete staff member profiles
  - Role-based access (Manager, Receptionist, Waiter, Chef, Cleaner, Security, Maintenance, Administrator)
  - Staff-to-table assignments
  - Active/inactive status tracking
  - Department management

- **Task Management**
  - Create and assign tasks to staff members
  - Task status tracking (Pending, In Progress, Completed, Cancelled)
  - Priority levels and due dates
  - Task filtering and search

- **Customer Ratings & Reviews**
  - Customer feedback submission
  - Rating approval workflow (Pending, Approved, Rejected)
  - Average rating calculation
  - Rating distribution analytics

- **Dashboard**
  - Real-time statistics and metrics
  - Quick access to all modules
  - Recent activity overview
  - Visual data representation

## 🛠 Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM framework
- **MySQL** - Relational database
- **Spring Validation** - Input validation

### Frontend
- **Thymeleaf** - Server-side template engine
- **Bootstrap 5.3.0** - CSS framework
- **Bootstrap Icons** - Icon library
- **JavaScript** - Client-side scripting

### Tools
- **Maven** - Build and dependency management
- **Spring Boot DevTools** - Development tools

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+** or higher
- **Git** (optional, for version control)

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Hms
```

### 2. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE hotel_management;
```

### 3. Configure Database

Update the database configuration in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hotel_management
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

**Option 1: Using Maven**
```bash
mvn spring-boot:run
```

**Option 2: Using Java**
```bash
java -jar target/hotel-management-system-1.0.0.jar
```

**Option 3: Using Maven Wrapper (Windows)**
```bash
.\mvnw.cmd spring-boot:run
```

**Option 4: Using Maven Wrapper (Linux/Mac)**
```bash
./mvnw spring-boot:run
```

### 6. Access the Application

Open your browser and navigate to:
```
http://localhost:8080
```

## ⚙️ Configuration

### Application Properties

Key configuration options in `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/hotel_management
spring.datasource.username=root
spring.datasource.password=your_password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Thymeleaf Configuration
spring.thymeleaf.cache=false
```

### Database Schema

The application uses JPA/Hibernate with `ddl-auto=update`, which automatically creates/updates database tables based on entity classes. No manual schema creation is required.

## 📁 Project Structure

```
Hms/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hotel/management/
│   │   │       ├── config/              # Configuration classes
│   │   │       ├── controller/          # REST/Web controllers
│   │   │       ├── entity/              # JPA entities
│   │   │       ├── enums/               # Enumeration classes
│   │   │       ├── repository/          # Data access layer
│   │   │       ├── service/             # Business logic layer
│   │   │       └── HotelManagementSystemApplication.java
│   │   └── resources/
│   │       ├── static/                  # Static resources (CSS, JS, images)
│   │       │   └── images/              # Logo and images
│   │       ├── templates/               # Thymeleaf templates
│   │       │   ├── index.html           # Dashboard
│   │       │   ├── navbar.html          # Navigation bar
│   │       │   ├── footer.html          # Footer
│   │       │   ├── reservations.html    # Reservations page
│   │       │   ├── staff.html           # Staff management
│   │       │   ├── tasks.html           # Task management
│   │       │   └── ratings.html         # Ratings page
│   │       └── application.properties   # Application configuration
│   └── test/                             # Test files
├── pom.xml                               # Maven configuration
└── README.md                             # Project documentation
```

## 💻 Usage

### Dashboard

The main dashboard provides:
- Overview statistics
- Quick access to all modules
- Recent reservations and ratings
- System metrics

### Managing Reservations

1. Navigate to **Reservations** from the navigation bar
2. Click **New Reservation** to create a new booking
3. Fill in customer details, select table, and choose date/time
4. View and manage existing reservations
5. Update reservation status (Confirm, Seat, Complete, Cancel)

### Managing Staff

1. Navigate to **Staff Management** from the navigation bar
2. Click **Add Staff Member** to add new staff
3. Assign tables to staff members
4. View staff assignments and manage roles
5. Activate/deactivate staff members

### Managing Tasks

1. Navigate to **Tasks** from the navigation bar
2. Create new tasks and assign to staff
3. Track task progress and completion
4. Filter tasks by status, priority, or assignee

### Managing Ratings

1. Navigate to **Ratings** from the navigation bar
2. View customer ratings and feedback
3. Approve or reject pending ratings
4. View rating statistics and distribution

## 🔌 API Endpoints

### Reservations
- `GET /reservations` - List all reservations
- `GET /reservations/new` - Show reservation form
- `POST /reservations` - Create new reservation
- `POST /reservations/{id}/confirm` - Confirm reservation
- `POST /reservations/{id}/seat` - Mark as seated
- `POST /reservations/{id}/complete` - Complete reservation
- `POST /reservations/{id}/cancel` - Cancel reservation

### Staff
- `GET /staff` - List all staff members
- `GET /staff/new` - Show staff form
- `POST /staff` - Create new staff member
- `GET /staff/{id}/edit` - Edit staff member
- `POST /staff/{id}/edit` - Update staff member
- `POST /staff/{staffId}/assign-table` - Assign table to staff
- `POST /staff/{staffId}/unassign-table` - Unassign table from staff

### Tasks
- `GET /tasks` - List all tasks
- `GET /tasks/new` - Show task form
- `POST /tasks` - Create new task
- `POST /tasks/{id}/complete` - Complete task
- `POST /tasks/{id}/cancel` - Cancel task

### Ratings
- `GET /ratings` - List all ratings
- `GET /ratings/new` - Show rating form
- `POST /ratings` - Submit new rating
- `POST /ratings/{id}/approve` - Approve rating
- `POST /ratings/{id}/reject` - Reject rating

## 🎨 Screenshots

### Dashboard
The main dashboard provides a comprehensive overview of the hotel management system with quick access to all modules.

### Reservation Management
Efficiently manage restaurant reservations with real-time table availability and status tracking.

### Staff Management
Complete staff management with role assignments and table assignments.

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java coding conventions
- Write meaningful commit messages
- Add comments for complex logic
- Ensure all tests pass before submitting PR
- Update documentation as needed

## 📝 License

This project is a proof of concept.

## 👥 Authors

- **Development Team** - Shravan Kumar(NNM23CS188)
                          Srujan Bangera(NNM23CS205)
                          Siri Udaya Shetty(NNM23CS203)
                          Shreyas (NNM23CS192)

## 🙏 Acknowledgments

- Spring Boot community
- Bootstrap team for the excellent CSS framework
- All contributors and testers

## 📞 Support

For support, email skushravan@gmail.com or create an issue in the repository.

## 🔄 Version History

- **v1.0.0** (2025)
  - Initial release
  - Core features: Reservations, Staff, Tasks, Ratings
  - Dashboard implementation
  - Table management
  - Customer ratings system

---

**Made with ❤️ for efficient hotel and restaurant management**
**Made by Humans on earth ❤️**

