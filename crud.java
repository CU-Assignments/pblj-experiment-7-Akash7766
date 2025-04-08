import java.sql.*;
import java.util.Scanner;

public class ProductManager {
    private static final String DB_URL = "jdbc:sqlite:products.db";

    public static void main(String[] args) {
        createTable();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n====== Product Management Menu ======");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Exit");
            System.out.print("Enter choice (1-5): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createProduct(scanner);
                    break;
                case "2":
                    readProducts();
                    break;
                case "3":
                    updateProduct(scanner);
                    break;
                case "4":
                    deleteProduct(scanner);
                    break;
                case "5":
                    System.out.println("Exiting.");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void createTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Product (" +
                    "ProductID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ProductName TEXT NOT NULL, " +
                    "Price REAL NOT NULL, " +
                    "Quantity INTEGER NOT NULL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    private static void createProduct(Scanner scanner) {
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();
        System.out.print("Enter product price: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter product quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine());

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);

            String sql = "INSERT INTO Product (ProductName, Price, Quantity) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, price);
                pstmt.setInt(3, quantity);
                pstmt.executeUpdate();
                conn.commit();
                System.out.println("Product added.");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error adding product: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void readProducts() {
        String sql = "SELECT * FROM Product";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("\n%-10s %-20s %-10s %-10s\n", "ProductID", "ProductName", "Price", "Quantity");
            System.out.println("----------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10d %-20s %-10.2f %-10d\n",
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getDouble("Price"),
                        rs.getInt("Quantity"));
            }
        } catch (SQLException e) {
            System.out.println("Error reading products: " + e.getMessage());
        }
    }

    private static void updateProduct(Scanner scanner) {
        System.out.print("Enter ProductID to update: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter new product name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new price: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter new quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine());

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);

            String sql = "UPDATE Product SET ProductName = ?, Price = ?, Quantity = ? WHERE ProductID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, price);
                pstmt.setInt(3, quantity);
                pstmt.setInt(4, id);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    conn.commit();
                    System.out.println("Product updated.");
                } else {
                    conn.rollback();
                    System.out.println("Product ID not found.");
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error updating product: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void deleteProduct(Scanner scanner) {
        System.out.print("Enter ProductID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);

            String sql = "DELETE FROM Product WHERE ProductID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    conn.commit();
                    System.out.println("Product deleted.");
                } else {
                    conn.rollback();
                    System.out.println("Product ID not found.");
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error deleting product: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
