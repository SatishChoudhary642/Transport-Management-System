# 🚛 Transport Management System (TMS) Backend

A robust Spring Boot backend for managing logistics operations, built for the CargoPro technical assignment. This system handles Loads, Transporters, Bids, and Booking management with complex business logic validation.

## 🚀 Features Implemented
* **CRUD Operations:** Full management for Loads and Transporters.
* **Complex Business Rules:**
    * **Capacity Validation:** Transporters cannot bid if they lack truck capacity.
    * **Status Transitions:** Automated lifecycle (POSTED → OPEN_FOR_BIDS → BOOKED).
    * **Concurrency Control:** Optimistic Locking (`@Version`) prevents double-booking.
    * **Best Bid Algorithm:** Smart sorting based on rate and rating.
* **Pagination & Filtering:** efficient data retrieval for large datasets.
* **Global Exception Handling:** Clean JSON error responses.

## 🛠️ Tech Stack
* **Java 17**
* **Spring Boot 3.2.3**
* **Spring Data JPA** (Hibernate)
* **PostgreSQL** (Database)
* **Lombok** (Boilerplate reduction)
* **Maven** (Build tool)

## 🏃‍♂️ How to Run

1.  **Prerequisites:** Ensure PostgreSQL is running and you have created a database named `tms`.
2.  **Configure Database:** Update `src/main/resources/application.properties` with your credentials:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/tms
    spring.datasource.username=postgres
    spring.datasource.password=your_password
    ```
3.  **Run the App:**
    ```bash
    ./mvnw spring-boot:run
    ```
4.  The server will start at `http://localhost:8080`.

## 📚 API Documentation

### 1. Load Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/load` | Create a new load (Default status: POSTED) |
| `GET` | `/load` | List loads (Filter by `shipperId`, `status`) |
| `GET` | `/load/{id}` | Get load details |
| `PATCH` | `/load/{id}/cancel` | Cancel a load |
| `GET` | `/load/{id}/best-bids` | Get bids sorted by best score |

### 2. Transporter Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/transporter` | Register a new transporter |
| `GET` | `/transporter/{id}` | Get transporter profile |
| `PUT` | `/transporter/{id}/trucks` | Update available truck inventory |

### 3. Bidding System
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/bid` | Submit a bid (Validates capacity & status) |
| `GET` | `/bid?loadId={id}` | View all bids for a specific load |
| `PATCH` | `/bid/{id}/reject` | Reject a specific bid |

### 4. Booking Operations
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/booking` | Accept a bid and create a booking |
| `PATCH` | `/booking/{id}/cancel` | Cancel booking & restore truck capacity |

## 🗄️ Database Schema
* **One-to-Many:** Load ↔ Bids
* **One-to-Many:** Transporter ↔ TransporterTrucks
* **Optimistic Locking:** The `loads` table uses a `version` column to handle concurrent write attempts.

## 🧪 Testing
Run the full test suite using:
```bash
./mvnw test