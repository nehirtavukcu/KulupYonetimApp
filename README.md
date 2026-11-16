
# Student Club and Event Management System

## Project Overview

The Student Club and Event Management System is a Java-based desktop application developed for the COME 381 course.

The application allows universities to manage students, student clubs, memberships, event planning, venues, attendance records, and certificate eligibility through a graphical user interface.

The system uses Java Swing for the user interface and JDBC for database operations.

## Features

### 1. Student Management
- List all registered students.
- Add new students.
- Delete selected students.
- Automatically delete related membership and attendance records using CASCADE constraints.

### 2. Club and Membership Management
- Display all registered clubs.
- View student memberships.
- Display membership information using SQL JOIN operations.
- Show student roles and membership dates.

### 3. Venue Management
- List available event venues.
- Display venue names, campuses, and capacities.

### 4. Event Planning
- Create new events.
- Select the organizing club.
- Select an event venue.
- Specify the event name, date, and time.
- Prevent venue scheduling conflicts using database constraints.

### 5. Attendance and Certificate Management
- Record student attendance at events.
- Prevent duplicate attendance records.
- Display all attendance records.
- Track certificate eligibility through the `vw_sertifika_durumu` database view.

## Technologies Used

- **Programming Language:** Java
- **GUI Framework:** Java Swing
- **Database Connectivity:** JDBC
- **Database:** SQL (MySQL-compatible)
- **IDE:** IntelliJ IDEA / Eclipse / NetBeans

## Database Structure

The application uses the following database tables:

| Table | Description |
|---|---|
| Ogrenciler | Stores student information |
| Kulupler | Stores student club information |
| Uyelikler | Stores student-club memberships |
| Mekanlar | Stores event venue information |
| Etkinlikler | Stores planned events |
| Katilimlar | Stores student attendance records |

### Database View

- `vw_sertifika_durumu`: Displays student attendance counts and certificate eligibility status.

## Database Relationships

- A student can have multiple club memberships.
- A club can have multiple student members.
- A club can organize multiple events.
- A venue can host multiple events at different times.
- Students can attend multiple events.
- Attendance records connect students and events.

## Installation and Setup

### 1. Clone or Download the Project

Download the project files or clone the repository.

### 2. Set Up the Database

Create the database and execute the SQL scripts to create the required tables, relationships, constraints, and views.

### 3. Configure Database Connection

Create a `DatabaseConnection` class to establish the JDBC connection.

Example:

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/kulup_yonetim";
        String username = "root";
        String password = "your_password";

        return DriverManager.getConnection(
            url, username, password
        );
    }
}
```

Update the database URL, username, and password according to your local configuration.

### 4. Add JDBC Driver

Add the MySQL JDBC Connector to your project's dependencies.

### 5. Run the Application

Run the `KulupYonetimApp.java` file to start the application.

## Important Database Constraints

The system uses database-level constraints to maintain data integrity:

- Primary Key (PK) constraints.
- Foreign Key (FK) constraints.
- CASCADE DELETE relationships.
- Unique constraints to prevent duplicate attendance records.
- Unique venue scheduling constraints to prevent overlapping event reservations.



