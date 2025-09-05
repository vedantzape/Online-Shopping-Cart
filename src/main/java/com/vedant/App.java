package com.vedant;



import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Scanner;

public class App {
    private static SessionFactory sessionFactory;
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Cart.class)
                .addAnnotatedClass(Product.class)
                .addAnnotatedClass(CartItem.class)
                .buildSessionFactory();

        while (true) {
            System.out.println("\n==== Online Shopping Cart ====");
            System.out.println("1. Register User");
            System.out.println("2. Add Product");
            System.out.println("3. View Products");
            System.out.println("4. Add to Cart");
            System.out.println("5. View Cart");
            System.out.println("6. Checkout");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> registerUser();
                case 2 -> addProduct();
                case 3 -> viewProducts();
                case 4 -> addToCart();
                case 5 -> viewCart();
                case 6 -> checkout();
                case 7 -> {
                    sessionFactory.close();
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("❌ Invalid choice!");
            }
        }
    }

    // ================= Methods ================= //

    private static void registerUser() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            sc.nextLine(); // clear buffer
            System.out.print("Enter name: ");
            String name = sc.nextLine();
            System.out.print("Enter email: ");
            String email = sc.nextLine();
            System.out.print("Enter password: ");
            String password = sc.nextLine();

            User user = new User(name, email, password);
            Cart cart = new Cart(user);
            user.setCart(cart);

            session.persist(user); // Cascade saves cart
            tx.commit();

            System.out.println("✅ User registered successfully!");
        }
    }

    private static void addProduct() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            sc.nextLine(); // clear buffer
            System.out.print("Enter product name: ");
            String name = sc.nextLine();
            System.out.print("Enter price: ");
            double price = sc.nextDouble();
            System.out.print("Enter stock: ");
            int stock = sc.nextInt();

            Product product = new Product(name, price, stock);
            session.persist(product);
            tx.commit();

            System.out.println("✅ Product added successfully!");
        }
    }

    private static void viewProducts() {
        try (Session session = sessionFactory.openSession()) {
            List<Product> products = session.createQuery("from Product", Product.class).list();
            System.out.println("\n--- Available Products ---");
            if (products.isEmpty()) {
                System.out.println("⚠️ No products available.");
            } else {
                for (Product p : products) {
                    System.out.printf("%d | %s | %.2f | Stock: %d%n",
                            p.getId(), p.getName(), p.getPrice(), p.getStock());
                }
            }
        }
    }

    private static void addToCart() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            System.out.print("Enter user ID: ");
            int userId = sc.nextInt();
            System.out.print("Enter product ID: ");
            int productId = sc.nextInt();
            System.out.print("Enter quantity: ");
            int qty = sc.nextInt();

            User user = session.find(User.class, userId);
            Product product = session.find(Product.class, productId);

            if (user == null || product == null) {
                System.out.println("❌ Invalid user or product!");
                tx.rollback();
                return;
            }
            if (product.getStock() < qty) {
                System.out.println("❌ Not enough stock!");
                tx.rollback();
                return;
            }

            Cart cart = user.getCart();
            CartItem item = new CartItem(cart, product, qty);
            cart.getItems().add(item); // maintain both sides

            session.persist(item);
            tx.commit();

            System.out.println("✅ Product added to cart!");
        }
    }

    private static void viewCart() {
        try (Session session = sessionFactory.openSession()) {
            System.out.print("Enter user ID: ");
            int userId = sc.nextInt();

            User user = session.find(User.class, userId);
            if (user == null) {
                System.out.println("❌ User not found!");
                return;
            }

            Cart cart = user.getCart();
            if (cart == null || cart.getItems().isEmpty()) {
                System.out.println("🛒 Cart is empty!");
                return;
            }

            System.out.println("\n--- Cart Items ---");
            for (CartItem item : cart.getItems()) {
                System.out.printf("%s | Qty: %d | Price: %.2f%n",
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getProduct().getPrice());
            }
        }
    }

    private static void checkout() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            System.out.print("Enter user ID: ");
            int userId = sc.nextInt();

            User user = session.find(User.class, userId);
            if (user == null) {
                System.out.println("❌ User not found!");
                tx.rollback();
                return;
            }

            Cart cart = user.getCart();
            if (cart == null || cart.getItems().isEmpty()) {
                System.out.println("🛒 Cart is empty!");
                tx.rollback();
                return;
            }

            double total = 0;
            for (CartItem item : cart.getItems()) {
                Product product = item.getProduct();

                if (product.getStock() < item.getQuantity()) {
                    System.out.println("❌ Not enough stock for product: " + product.getName());
                    tx.rollback();
                    return;
                }

                // Update stock
                product.setStock(product.getStock() - item.getQuantity());
                session.merge(product);

                total += product.getPrice() * item.getQuantity();
            }

            // Clear cart items
            cart.getItems().clear();
            session.merge(cart);

            tx.commit();
            System.out.println("✅ Checkout successful! Total Bill: " + total);
        }
    }
}
