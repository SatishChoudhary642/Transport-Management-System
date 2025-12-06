# 🚛 Transport Management System (TMS)

A robust Spring Boot backend for managing logistics operations. This system handles Loads, Transporters, Bids, and Booking management with complex business logic validation and concurrency control.

## 👤 Author
**Name:** Satish Choudhary
**Email:** satishchoudhary642@gmail.com
**GitHub:** [[link](https://github.com/SatishChoudhary642/Transport-Management-System/tree/main)]

## 🛠️ Tech Stack
* **Java 17** & **Spring Boot 3.2.3**
* **PostgreSQL** (Database)
* **Spring Data JPA** (Hibernate)
* **Maven** (Build tool)
* **Validation API** (Data integrity)

---

## 🗄️ Database Schema Diagram

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
        string shipper_id
        string loading_city
        string unloading_city
        timestamp loading_date
        string product_type
        double weight
        string weight_unit
        string truck_type
        int no_of_trucks
        string status
        timestamp date_posted
        long version "Optimistic Lock"
    }
    TRANSPORTER {
        UUID transporter_id PK
        string company_name
        double rating
    }
    TRANSPORTER_TRUCK {
        UUID id PK
        UUID transporter_id FK "Link to Parent"
        string truck_type
        int truck_count
    }
    BID {
        UUID bid_id PK
        UUID load_id FK
        UUID transporter_id FK
        double proposed_rate
        int trucks_offered
        string status
        timestamp submitted_at
    }
    BOOKING {
        UUID booking_id PK
        UUID load_id FK
        UUID bid_id FK
        UUID transporter_id FK
        int allocated_trucks
        double final_rate
        string status
        timestamp booked_at
    }