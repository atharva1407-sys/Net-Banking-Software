# SecureBank — Online Banking Management System

An educational, full-stack online banking simulation inspired by modern Indian net-banking user experiences.

---

## 🚀 Quick Start (One-Click)

Simply double-click:
```cmd
Start.bat
```
This batch script automatically:
1. Verifies Java and Node.js runtimes.
2. Starts the **Spring Boot API** on port `8080`.
3. Starts the **Angular NetBanking Portal** on port `4200`.

---

## 🌐 URLs & Access

- **Frontend NetBanking Portal:** [http://localhost:4200](http://localhost:4200)
- **Interactive Swagger / OpenAPI Docs:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **H2 In-Memory DB Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:securebank`, User: `sa`, Password: empty)

---

## 🔑 Synthetic Demo Credentials

| Role | Customer ID / Username | Password | Full Name | Primary Account |
| :--- | :--- | :--- | :--- | :--- |
| Customer 1 | `customer101` | `Password@123` | Aarav Sharma | Savings •••• 2345 (₹50,000.00) |
| Customer 2 | `customer102` | `Password@123` | Priya Patel | Current •••• 4826 (₹1,20,000.00) |

*You can also register a brand new account using the NetBanking **Sign Up** tab on the login screen.*

---

## 🏛 Architecture & Features

### Frontend (`frontend/`):
- **Angular standalone architecture** with strict TypeScript typing and reactive signals.
- **Features implemented:**
  - **Login & Registration:** Connected directly to Spring Boot with real-time error feedback, JWT session token management, and password toggles.
  - **Customer Dashboard:** Authoritative account balances, quick-actions, payee quick transfer strip, and recent transaction stream.
  - **Account Management:** List accounts, verify balances, and open secondary savings/current accounts.
  - **Fund Transfers:** Two-stage transfer with balance checks, saved beneficiary lookup, confirmation review, and receipt generation with reference codes.
  - **Transaction Ledger:** Paginated ledger with filtering by account and direction (debit/credit) and keyword search.
  - **Account Statements:** Custom date range statement generation and one-click direct **CSV Export**.
  - **Beneficiary Directory:** Save, search, and delete verified payees.
  - **In-App Notifications:** Real-time transfer alerts and account activity notifications.

### Backend (`backend/`):
- **Spring Boot 3.4.3 & Java 21+**
- **Spring Security:** Stateless JWT authentication (`io.jsonwebtoken`), BCrypt password hashing, rate limiting/lockout protection after 5 failed attempts.
- **Spring Data JPA & Hibernate:** Clean separation of concerns with Controllers, Services, Entities, Repositories, and DTOs.
- **Database:** PostgreSQL production profile + Flyway database migrations (`V1__init_schema.sql`), with embedded PostgreSQL-compatible H2 development profile for zero-friction local execution.
- **Transactional Integrity:** Balanced double-entry ledger bookkeeping on every transfer (`READ_COMMITTED` transaction isolation).
