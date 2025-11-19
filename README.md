# **CommerceFlow – Spring Boot Backend**

CommerceFlow is a simple Spring Boot backend built using **Maven**, **Java**, and **Spring Boot 3.4.11**. It provides REST APIs for managing products, categories, customers, orders, and order items.

---

## **Tech Stack**

* Java
* Spring Boot 3.4.11
* Spring Web
* Spring Data JPA
* Lombok
* Maven

---

## **Project Metadata**

```
Group: com.example
Artifact: commerceflow
```

---

## **Features**

* Clean REST API structure
* CRUD for Products, Categories, Customers, Orders, Order Items
* JPA + Hibernate for database operations
* Lombok for cleaner code

---

## **Run the Project**

```bash
mvn spring-boot:run
```

---

## **Database Config (PostgreSQL)**

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/commerceflow
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

---

