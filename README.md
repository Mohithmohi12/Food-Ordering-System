# Food Ordering System

A Spring Boot-based backend application for managing a food ordering platform. The project exposes REST APIs for handling users, customers, restaurants, menu items, orders, order items, and payments.

## Overview

This application is designed to support a food delivery or online ordering workflow where:

- customers can be created and managed
- restaurants can be added and searched
- menu items can be associated with restaurants
- orders are placed for customers
- each order can contain multiple order items
- payments can be tracked and updated by status
- users can register through the API

The project follows a layered architecture with:

- controllers for REST endpoints
- services for business logic
- repositories for database access
- entities for domain models
- exception handling for invalid or missing data

## Tech Stack

- Java
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok

## Project Structure

```text
FoodOrderingSystem/
├── src/
│   ├── main/
│   │   ├── java/com/springboot/FoodOrderingSystem/
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   └── FoodOrderingSystemApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Prerequisites

Before running this project, make sure you have:

- Java JDK installed
- Maven installed
- PostgreSQL database server installed and running
- a database named `food_ordering_db` created locally

## Database Configuration

The project connects to PostgreSQL using the settings in `src/main/resources/application.properties`.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/food_ordering_db
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

If your local PostgreSQL username/password is different, update the values accordingly.

## How to Run the Project

1. Open the terminal in the project root.
2. Make sure PostgreSQL is running.
3. Run the application:

```bash
./mvnw spring-boot:run
```

On Windows, you can also use:

```bash
mvnw.cmd spring-boot:run
```

The application will start on the default Spring Boot port:

```text
http://localhost:8080
```

## Main API Modules

The backend exposes REST APIs grouped by feature.

### 1. User APIs

Base path: `/api/users`

- `POST /api/users/register` — register a new user

### 2. Customer APIs

Base path: `/api/customers`

- `POST /api/customers` — add a customer
- `GET /api/customers` — get all customers
- `GET /api/customers/{id}` — get one customer by ID
- `PUT /api/customers/{id}` — update customer details
- `DELETE /api/customers/{id}` — delete a customer
- `GET /api/customers/contact/{contact}` — find customer by contact number
- `GET /api/customers/email/{email}` — find customer by email
- `GET /api/customers/name/{name}` — find customer by name

### 3. Restaurant APIs

Base path: `/api/restaurants`

- `POST /api/restaurants` — add restaurant
- `GET /api/restaurants` — get all restaurants
- `GET /api/restaurants/{id}` — get restaurant by ID
- `PUT /api/restaurants/{id}` — update restaurant
- `DELETE /api/restaurants/{id}` — delete restaurant
- `GET /api/restaurants/location/{location}` — search by location
- `GET /api/restaurants/name/{name}` — search by name
- `GET /api/restaurants/rating/{rating}` — get restaurants above a rating
- `GET /api/restaurants/{id}/menu` — get restaurant menu

### 4. Menu Item APIs

Base path: `/api/menu-items`

- `POST /api/menu-items/restaurant/{restaurantId}` — add menu item to a restaurant
- `GET /api/menu-items` — get all menu items
- `GET /api/menu-items/{id}` — get menu item by ID
- `PUT /api/menu-items/{id}` — update menu item
- `GET /api/menu-items/sort-by-price` — sort by price
- `GET /api/menu-items/name/{name}` — search by name
- `GET /api/menu-items/restaurant/{restaurantId}` — get menu items by restaurant

### 5. Order APIs

Base path: `/api/orders`

- `POST /api/orders` — create a new order
- `GET /api/orders` — get all orders
- `GET /api/orders/customer/{customerId}` — get orders by customer
- `GET /api/orders/{id}` — get order by ID
- `PUT /api/orders/{id}/status` — update order status
- `PUT /api/orders/{id}/cancel` — cancel an order
- `GET /api/orders/status/{status}` — get orders by status
- `GET /api/orders/date/{date}` — get orders by date
- `GET /api/orders/restaurant/{restaurantId}` — get orders by restaurant

### 6. Order Item APIs

Base path: `/api/order-items`

- `POST /api/order-items/order/{orderId}` — add item to an order
- `PUT /api/order-items/{orderItemId}/quantity` — update quantity
- `DELETE /api/order-items/{orderItemId}` — delete an order item
- `GET /api/order-items/order/{orderId}` — get items for an order

### 7. Payment APIs

Base path: `/api/payments`

- `POST /api/payments/order/{orderId}` — create payment for an order
- `GET /api/payments/{id}` — get payment by ID
- `GET /api/payments/order/{orderId}` — get payment for an order
- `GET /api/payments/status/{status}` — get payments by status
- `GET /api/payments/method/{method}` — get payments by method
- `PUT /api/payments/{id}/status` — update payment status

## Example Request

### Register User

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePass123"
}
```

### Create Restaurant

```json
{
  "name": "Pizza Corner",
  "location": "Bengaluru",
  "rating": 4.8
}
```

## Notes

- The project uses `spring.jpa.hibernate.ddl-auto=update`, so tables are created or updated automatically based on the entity mappings.
- Validation and custom exceptions are handled through the exception package to return meaningful error messages.
- This is a backend REST API project and is best used with tools like Postman or Swagger/OpenAPI-compatible clients.

## Future Improvements

- add authentication and authorization
- include Swagger UI for API documentation
- add validation for request payloads
- implement image uploads for menu items
- add order total calculations and discounts

## License

This project is currently a learning/demo backend application and can be modified or extended as needed.
