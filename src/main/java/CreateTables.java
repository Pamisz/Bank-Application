import java.sql.*;

public class CreateTables {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://localhost/users";
        String username = "pamisz";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database has been connected!");

            String createAccountTable = "CREATE TABLE IF NOT EXISTS account (" +
                    "accountNumber INT PRIMARY KEY AUTO_INCREMENT, " +
                    "balance DOUBLE NOT NULL DEFAULT 0.00, " +
                    "interest DOUBLE NOT NULL DEFAULT 0.02" +
                    ")";

            try (PreparedStatement pstmt = conn.prepareStatement(createAccountTable)) {
                pstmt.executeUpdate();
                System.out.println("Table account created!");
            }

            String createUserTable = "CREATE TABLE IF NOT EXISTS user (" +
                    "SSN VARCHAR(9) PRIMARY KEY, " +
                    "password VARCHAR(50) NOT NULL, " +
                    "firstName VARCHAR(50) NOT NULL, " +
                    "lastName VARCHAR(50) NOT NULL, " +
                    "accountNumber INT, " +
                    "FOREIGN KEY (accountNumber) REFERENCES account(accountNumber) " +
                    "ON DELETE CASCADE " +
                    "ON UPDATE CASCADE" +
                    ")";

            try (PreparedStatement pstmt = conn.prepareStatement(createUserTable)) {
                pstmt.executeUpdate();
                System.out.println("Table user created!");
            }

            String insertUser = "INSERT INTO user (SSN, password, firstName, lastName, accountNumber) VALUES ('root', 'root', 'root', 'root', NULL)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertUser)) {
                pstmt.executeUpdate();
                System.out.println("Admin inserted successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}