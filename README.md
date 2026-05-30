# ShareMyCar Management System

A standalone, console-based car-sharing management application written in Java, using an embedded H2 database.  
Handles vehicle inventory, bookings, returns, maintenance scheduling, transaction logging, and real-time financial reporting.

---

## Table of Contents

1. [Features](#features)
2. [Architecture & Package Structure](#architecture--package-structure)
3. [System Requirements Covered](#system-requirements-covered)
4. [Prerequisites](#prerequisites)
5. [Configuration](#configuration)
6. [Building & Running](#building--running)
7. [Usage & UI](#usage--ui)
8. [Database Schema](#database-schema)
9. [Code Documentation & Style](#code-documentation--style)
10. [Graceful Shutdown](#graceful-shutdown)

---

## Features

- **Vehicle Inventory Management**
    - View, add, delete vehicles
    - Track brand, model, mileage, daily rental price, maintenance cost/km, availability

- **Booking Functionality**
    - Create bookings with customer name, vehicle ID, duration (days), estimated kilometers
    - Automatic availability lock and cost estimation

- **Return Processing**
    - Record actual kilometers, calculate late fees (€10/day), cleaning fee (€20), variable maintenance cost
    - Auto-mark vehicle for maintenance if mileage crosses 10 000 km thresholds  
- **Maintenance Scheduling**
    - Vehicles flagged unavailable when crossing every 10 000 km
    - Maintenance cost logged per return

- **Transaction Logs**
    - Persist customer name, vehicle ID, rental duration, revenue, cleaning, maintenance, late fees, return date
    - Provides raw data for reporting.

- **Financial Metrics & Reporting**
    - Real-time aggregates: total revenue, total costs (cleaning + maintenance + late fees), profit, average mileage
    - Console report on demand
  
- **Data Seeding & Admin Controls**
    - On first startup (empty DB), seeds 10 default vehicles.
    - **Reset database** option: drops & recreates all tables, then reseeds defaults.

- **Layered Architecture**
    - Clear separation of UI, service, repository, and model layers.
    - Automatic schema creation (DAOs create tables if missing).


## Architecture & Package Structure

- **Layers**
    - **Model**: data structures
    - **Repository**: JDBC/H2 persistence
    - **Service**: business logic
    - **UI**: console interaction

The ShareMyCar system is organized into the following packages:

- **com.sharemycar.model**: Contains the data models for the application.
    - `Vehicle.java`: Represents a vehicle in the fleet.
    - `Booking.java`: Represents a booking made by a customer.
    - `ReturnRecord.java`: Represents the record of a vehicle return.
    - `TransactionLog.java`: Represents a log of a transaction.

- **com.sharemycar.repository**: Handles data persistence using JDBC with H2 database.
    - `VehicleRepository.java`: Manages CRUD operations for vehicles.
    - `BookingRepository.java`: Manages CRUD operations for bookings.
    - `TransactionLogRepository.java`: Manages logging of transactions.

- **com.sharemycar.service**: Implements the business logic of the application.
    - `FleetService.java`: Handles operations related to the vehicle fleet.
    - `BookingService.java`: Manages booking operations.
    - `ReturnService.java`: Handles vehicle return processing, including fee calculations and maintenance checks.
    - `ReportingService.java`: Generates financial reports.
    - `AdminService.java`: Provides administrative functions, such as resetting the database.

- **com.sharemycar.ui**: Manages the user interface, which is console-based.
    - `ConsoleApp.java`: Implements the console menu and user interactions.

Additionally, the main entry point of the application is:
- `Main.java`: The class that starts the application.


---

## System Requirements Covered

| #  | Requirement                                             | How It’s Implemented                                      |
|----|---------------------------------------------------------|------------------------------------------------------------|
| 1  | Vehicle inventory management                            | `VehicleRepository` + `FleetService` + ConsoleApp (options 1,2,6) |
| 2  | Booking functionality                                   | `BookingService` + ConsoleApp (option 3)                  |
| 3  | Return processing (fees, logging, availability updates) | `ReturnService` + ConsoleApp (option 4)                   |
| 4  | Maintenance scheduling                                   | `ReturnService` checks 10 000 km threshold + flags vehicle |
| 5  | Transaction logs                                        | `TransactionLogRepository` + `ReturnService` logs per return |
| 6  | Financial metrics & reports                             | `ReportingService` + ConsoleApp (option 5)                |


---

## Prerequisites

- - **Java 21** (tested)
- **Maven** (for build & dependency management)

---

## Configuration

Created a file `src/main/resources/application.properties`:

```properties
# H2 embedded database URL (file-based)
jdbc.url=jdbc:h2:./sharemycar;AUTO_SERVER=TRUE
jdbc.user=sa
jdbc.pass=
```
---
## Building & Running

### Build

```bash
mvn clean package
```
Produces target/sharemycar-1.0-SNAPSHOT.jar.

### Run the application
From the project root (or wherever you have the JAR):
```bash
java -jar target/sharemycar-1.0-SNAPSHOT.jar
```

---
## Usage & UI
The console menu, displayed on startup, offers nine options:

1. View full vehicle inventory, listing all vehicles.
2. Add a new vehicle, prompting for details.
3. Book a vehicle, creating a new booking.
4. Return a vehicle, processing the return with fees.
5. Generate financial report, displaying metrics.
6. Delete a vehicle, removing it from inventory.
7. View booking details, listing all bookings.
8. Reset database, dropping and recreating tables with default data.
9. Exit, shutting down gracefully.

##### Type the number and press Enter.

### Examples:

-  To list all vehicles, enter `1`.  
-  To make a booking, enter `3` and follow prompts.  
-  To return a car, enter `4` and enter the booking ID + actual kilometers driven.  
-  To see revenue and costs, enter `5`.  
-  To wipe and reseed your database, enter `8` (you’ll be asked to confirm).  
-  To quit, enter `9`.


---
## Database Schema
```sql
-- vehicles
CREATE TABLE vehicles (
id INT AUTO_INCREMENT PRIMARY KEY,
brand VARCHAR(100),
model VARCHAR(100),
mileage DOUBLE,
daily_price DOUBLE,
maintenance_cost_per_km DOUBLE,
available BOOLEAN
);

-- bookings
CREATE TABLE bookings (
id INT AUTO_INCREMENT PRIMARY KEY,
customer_name VARCHAR(100),
vehicle_id INT,
start_date DATE,
duration_days INT,
estimated_km DOUBLE,
estimated_cost DOUBLE,
returned BOOLEAN DEFAULT FALSE
);

-- transaction_logs
CREATE TABLE transaction_logs (
id INT AUTO_INCREMENT PRIMARY KEY,
customer_name VARCHAR(100),
vehicle_id INT,
duration_days INT,
revenue DOUBLE,
cleaning_fee DOUBLE,
maintenance_cost DOUBLE,
late_fee DOUBLE,
transaction_date DATE
);
```

The schema uses AUTO_INCREMENT for primary keys and appropriate data types, ensuring data integrity

---

## Code Documentation & Style

- Javadoc on all classes and public methods, describing purpose, parameters, and behavior.

- Inline comments explain logic and SQL operations.

- Text blocks ("""…""") for multi-line SQL (Java 15+).

- Try-with-resources for safe JDBC resource cleanup.

- Switch expressions (Java 14+) in ConsoleApp.

- Leverages Java 21 wherever possible—e.g., pattern matching in switch (if extended), sealed types, etc.

---

## Graceful Shutdown
- Exit via menu option 9 prints a farewell and returns from main().

- All JDBC resources are closed automatically via try-with-resources.

- Startup failures (e.g., missing application.properties or DB errors) log an error and exit.
---


### Viewing the README

- **GUI**: On Windows/macOS/Linux, just double-click `README.md` to open it in your default text or Markdown viewer (Notepad, TextEdit, VS Code, etc.).
- **Command-line**:
  - macOS/Linux: `less README.md` or `cat README.md`
  - Windows PowerShell: `more README.md` or `type README.md`
- **In an Editor**: Any Markdown-aware IDE (VS Code, IntelliJ, Atom) will render headings, code blocks, and lists nicely.
---
