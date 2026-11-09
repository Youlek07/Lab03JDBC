import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Form {
    private JPanel panel1;
    private JPanel topPanel;
    private JTextField nameTextField;
    private JTextField surnameTextField;
    private JTextField positionTextField;
    private JButton addButton;
    private JTable employeesTable;
    private JScrollPane employeesTableScrollPane;

    private DefaultTableModel model;

    private final String URL = "jdbc:sqlite:employees.db";

    public Form() {

        model = new DefaultTableModel(new String[]{"ID", "Imię", "Nazwisko", "Stanowisko"}, 0);

        employeesTable.setModel(model);

        createTable();
        loadEmployees();

        addButton.addActionListener(e -> addEmployee());
    }

    private void createTable() {

        String sql = """
            CREATE TABLE IF NOT EXISTS employees (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                surname TEXT NOT NULL,
                position TEXT NOT NULL
            )
            """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, e.getMessage());
        }
    }

    private void loadEmployees() {

        model.setRowCount(0);

        String sql = "SELECT * FROM employees";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("position")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, e.getMessage());
        }
    }

    private void addEmployee() {

        String name = nameTextField.getText().trim();
        String surname = surnameTextField.getText().trim();
        String position = positionTextField.getText().trim();

        if (name.isEmpty() || surname.isEmpty() || position.isEmpty()) {

            JOptionPane.showMessageDialog(panel1, "Uzupełnij wszystkie pola!", "Błąd", JOptionPane.WARNING_MESSAGE);

            return;
        }

        String sql = "INSERT INTO employees (name, surname, position) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.setString(3, position);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(panel1, "Dodano pracownika!");

            nameTextField.setText("");
            surnameTextField.setText("");
            positionTextField.setText("");

            loadEmployees();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(panel1, "Błąd SQL: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("INF.04 - System Obsługi Pracowników");

        frame.setContentPane(new Form().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
