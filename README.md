# IntelliCart 🛒 - Scalable E-Commerce Backend (MVP)

**IntelliCart** is a production-ready, modular backend system for a modern e-commerce platform. This project serves as a portfolio piece to demonstrate best practices in backend engineering using **Java Spring Boot**, **MySQL**, and a clean, layered architecture.  

> ⚠️ **Note:** This is the **backend MVP** of IntelliCart. Frontend integration can be added later.

---

## Key Features ✨

- 🔐 Secure Authentication: JWT-based authentication with BCrypt password hashing and role-based access control (`ADMIN`, `CUSTOMER`).
- 📦 Product Management: Full CRUD functionality for the product catalog, accessible only to admins.
- ⚡ Efficient Search: Implements Binary Search for product lookups, with a structure ready for future advanced search upgrades (e.g., Tries).
- 🛍️ Dynamic Shopping Cart: Per-user cart system with real-time inventory checks to prevent overselling.
- 💳 Transactional Order Processing: Ensures atomicity and data consistency when placing orders, deducting stock, and recording transactions.
- 📊 Admin Analytics API: Secure endpoints for administrators to retrieve metrics like total sales, top-selling products, and most active users.

---

## Architecture & Tech Stack

| Component | Technology |
|-----------|------------|
| Backend | Java 17, Spring Boot 3.x |
| Database | MySQL 8 |
| ORM | Spring Data JPA (Hibernate) |
| Authentication | Spring Security, JSON Web Tokens (JWT) |
| Build Tool | Maven |
| Deployment | Render |

---

## Getting Started

Follow these instructions to get the backend running locally.

### Prerequisites
- Java JDK 17+
- Apache Maven
- MySQL Server
- API client (e.g., Postman)

### 1. Clone the Repository
git clone https://github.com/YourUsername/IntelliCart-Backend.git  
cd IntelliCart-Backend

### 2. Database Setup
Ensure your MySQL server is running. Then, execute the provided SQL scripts:  
SOURCE sql/schema.sql;  
SOURCE sql/data.sql;

### 3. Configuration
Edit `src/main/resources/application.properties` to match your local database credentials:  
spring.datasource.username=your_mysql_username  
spring.datasource.password=your_mysql_password  

*(For security, use environment variables for sensitive data like JWT secret.)*

### 4. Run the Application
mvn spring-boot:run  

The backend will start on `http://localhost:8080`.

---

## API Usage & Endpoints 🚀

You can test all endpoints using Postman by importing the provided `intellicart_postman_collection.json`.

### Example Requests

**1. Login as Customer**  
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username": "customer1", "password": "custpass"}'  

(Returns a JWT token for authentication.)

**2. Get All Products**  
curl http://localhost:8080/api/products  

**3. Add an Item to Cart (Requires Auth)**  
curl -X POST http://localhost:8080/api/cart/add -H "Authorization: Bearer <your_jwt_token>" -H "Content-Type: application/json" -d '{"productId": 1, "quantity": 1}'

---

## Deployment on Render

1. Push the project to GitHub.  
2. Create a new **Web Service** on Render and connect your repository.  
3. Create a MySQL database on Render and copy the internal URL.  
4. Set environment variables in Render:  

SPRING_DATASOURCE_URL=your_render_mysql_url  
SPRING_DATASOURCE_USERNAME=your_username  
SPRING_DATASOURCE_PASSWORD=your_password  
JWT_SECRET=<strong_random_secret>  

Render will automatically build and deploy the backend.

---

## Scalability & Future Enhancements

- Caching: Add Redis for frequently accessed data (products, search results).  
- Async Operations: Use Kafka or RabbitMQ for order notifications or email processing.  
- Database Scaling: Introduce read replicas for heavy read operations.  
- Containerization: Docker + Kubernetes for deployment and orchestration.  
- Frontend Integration: Build a React/Vue frontend to complete IntelliCart as a full-stack application.  

---

## Project Status

- ✅ Backend MVP complete  
- ⚠️ Frontend integration pending  
- ⚡ Ready for deployment and API testing  
- 📝 Resume-worthy backend project demonstrating Spring Boot, MySQL, JWT, and scalable design  

---

MVP IntelliCart backend generated successfully — run `mvn spring-boot:run` to start the server.
