# 🚛 Transport Management System (TMS)

A robust Spring Boot backend for managing logistics operations. This system handles Loads, Transporters, Bids, and Booking management with complex business logic validation and concurrency control.

## 👤 Author
**Name:** Satish
**Email:** [Your Email Here]
**GitHub:** [Link to this Repo]

## 🛠️ Tech Stack
* **Java 17** & **Spring Boot 3.2.3**
* **PostgreSQL** (Database)
* **Spring Data JPA** (Hibernate)
* **Maven** (Build tool)
* **Validation API** (Data integrity)

---

## 🗄️ Database Schema Diagram
*(Rendered automatically by GitHub)*

```mermaid
erDiagram
    LOAD ||--o{ BID : "receives"
    LOAD ||--o{ BOOKING : "has"
    TRANSPORTER ||--o{ TRANSPORTER_TRUCK : "owns"
    TRANSPORTER ||--o{ BID : "places"
    TRANSPORTER ||--o{ BOOKING : "fulfills"
    BID ||--|| BOOKING : "becomes"

    LOAD {
        UUID load_id PK
        string status "POSTED, BOOKED..."
        int version "Optimistic Lock"
    }
    TRANSPORTER {
        UUID transporter_id PK
        string company_name
    }
    BID {
        UUID bid_id PK
        double amount
        string status
    }
    BOOKING {
        UUID booking_id PK
        timestamp booked_at
    }