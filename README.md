# 🛒 Online Shopping Cart (Hibernate + MySQL + Java)

A console-based **Online Shopping Cart System** built using **Java, Hibernate (JPA), and MySQL**.  
This project demonstrates **ORM mapping, entity relationships, transaction management, and CRUD operations** with Hibernate.

---

## 🚀 Features
- 👤 **User Management**  
  - Register new users with name, email, and password.  
  - Each user automatically gets an empty cart.  

- 📦 **Product Management**  
  - Add new products with name, price, and stock.  
  - View all available products.  

- 🛒 **Shopping Cart**  
  - Add products to user’s cart.  
  - View all cart items for a user.  
  - Checkout and update stock.  

---

## 🏗️ Tech Stack
- **Java 17+**  
- **Hibernate 6 (JPA)**  
- **MySQL 8**  
- **Maven** (for dependencies)

---

## 📂 Project Structure
src/main/java/
├── App.java # Main application (console-based menu)
├── model/
│ ├── User.java
│ ├── Cart.java
│ ├── Product.java
│ └── CartItem.java
src/main/resources/
└── hibernate.cfg.xml # Hibernate configuration


---

## 🔗 Entity Relationships
- **User ↔ Cart** → One-to-One  
- **Cart ↔ CartItem** → One-to-Many  
- **CartItem ↔ Product** → Many-to-One  

---

## ⚙️ Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/online-shopping-cart.git
cd online-shopping-cart


2. Configure Database

Create a MySQL database:

CREATE DATABASE shoppingdb;


Update src/main/resources/hibernate.cfg.xml with your credentials:

<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/shoppingdb</property>
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">yourpassword</property>

3. Build & Run
mvn clean compile exec:java -Dexec.mainClass="App"

📋 Menu Options
==== Online Shopping Cart ====
1. Register User
2. Add Product
3. View Products
4. Add to Cart
5. View Cart
6. Checkout
7. Exit

🛠️ Future Enhancements

✅ Add user authentication (login/logout).

✅ Generate invoice on checkout.

✅ Migrate to Spring Boot + REST APIs.

✅ Add React frontend for UI.
