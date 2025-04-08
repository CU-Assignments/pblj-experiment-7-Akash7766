import java.sql.*;
import java.util.*;

// ===== Model =====
class Student {
    private int studentID;
    private String name;
    private String department;
    private double marks;

    public Student(int studentID, String name, String department, double marks) {
        this.studentID = studentID;
        this.name = name;
        this.department = department;
        this.marks = marks;
    }

    public Student(String name, String department, double marks) {
        this.name = name;
        this.department = department;
        this.marks = marks;
    }

    public int getStudentID() { return studentID; }
    public void setStudentID(int studentID) { this.studentID = studentID; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getMarks() { return marks; }
    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setMarks(double marks) { this.marks = marks; }
}

// ===== Controller =====
class StudentController {
    private static final String DB_URL = "jdbc:sqlite:students.db";

    public StudentController() {
        createTable();
    }

    private void createTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Students (" +
                    "StudentID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "Name TEXT NOT NULL," +
                    "Department TEXT NOT NULL," +
                    "Marks REAL NOT NULL)");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    public void addStudent(Student student) {
        String sql = "INSERT INTO Students (Name, Department, Marks) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getDepartment());
            pstmt.setDouble(3, student.getMarks());
            pstmt.executeUpdate();
            System.out.println("Student added.");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM Students";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("StudentID"),
                        rs.getString("Name"),
                        rs.getString("Department"),
                        rs.getDouble("Marks")));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving students: " + e.getMessage());
        }
        return list;
    }

    public void updateStudent(Student student) {
        String sql = "UPDATE Students SET Name = ?, Department = ?, Marks = ? WHERE StudentID = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getDepartment());
            pstmt.setDouble(3, student.getMarks());
            pstmt.setInt(4, student.getStudentID());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Student updated.");
            } else {
                System.out.println("Student ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }

    public void deleteStudent(int studentID) {
        String sql = "DELETE FROM Students WHERE StudentID = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentID);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Student deleted.");
            } else {
                System.out.println("Student ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
        }
    }
}

// ===== View & Main =====
public class StudentManager {
    public static void main(String[] args) {
        StudentController controller = new StudentController();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Student Management Menu =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter department: ");
                    String dept = scanner.nextLine();
                    System.out.print("Enter marks: ");
                    double marks = Double.parseDouble(scanner.nextLine());
                    controller.addStudent(new Student(name, dept, marks));
                    break;

                case "2":
                    List<Student> students = controller.getAllStudents();
                    System.out.printf("\n%-10s %-20s %-15s %-10s\n", "ID", "Name", "Department", "Marks");
                    for (Student s : students) {
                        System.out.printf("%-10d %-20s %-15s %-10.2f\n", s.getStudentID(), s.getName(), s.getDepartment(), s.getMarks());
                    }
                    break;

                case "3":
                    System.out.print("Enter student ID to update: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter new department: ");
                    String newDept = scanner.nextLine();
                    System.out.print("Enter new marks: ");
                    double newMarks = Double.parseDouble(scanner.nextLine());
                    controller.updateStudent(new Student(id, newName, newDept, newMarks));
                    break;

                case "4":
                    System.out.print("Enter student ID to delete: ");
                    int delId = Integer.parseInt(scanner.nextLine());
                    controller.deleteStudent(delId);
                    break;

                case "5":
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
