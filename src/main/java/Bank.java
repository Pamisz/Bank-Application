import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

public class Bank {
    private final Connection conn;

    public Bank() throws SQLException {
        String databasePassword = "password";
        String databaseUsername = "pamisz";
        String url = "jdbc:mysql://localhost/users";
        conn = DriverManager.getConnection(url, databaseUsername, databasePassword);
    }

    public Connection getConnection() {return this.conn;}

    public Customer getCustomer(String ssn) throws SQLException {
        String prompt= "SELECT SSN, password, firstName, lastName, accountNumber FROM user WHERE SSN = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(prompt)) {
            pstmt.setString(1, ssn);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int accountNumber = rs.getInt("accountNumber");
                    String prompt2 = "SELECT balance, interest FROM account WHERE accountNumber = ?";
                    try (PreparedStatement pstmt2 = conn.prepareStatement(prompt2)) {
                        pstmt2.setInt(1, accountNumber);
                        try (ResultSet rs2 = pstmt2.executeQuery()) {
                            if (rs2.next()) {
                                Account account = new Account(
                                        rs2.getDouble("balance"),
                                        rs2.getDouble("interest"),
                                        accountNumber
                                );

                                return new Customer(
                                        rs.getString("firstName"),
                                        rs.getString("lastName"),
                                        rs.getString("SSN"),
                                        rs.getString("password"),
                                        account
                                );
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    public void addCustomer(String firstName, String lastName, String ssn, String password) throws SQLException {
        String sql = "INSERT INTO account () VALUES ()";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int accountNumber = rs.getInt(1);

                    //Creating user after successfully created account
                    String insertUser = "INSERT INTO user (SSN, password, firstName, lastName, accountNumber) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt2 = conn.prepareStatement(insertUser)) {
                        pstmt2.setString(1, ssn);
                        pstmt2.setString(2, password);
                        pstmt2.setString(3, firstName);
                        pstmt2.setString(4, lastName);
                        pstmt2.setInt(5, accountNumber);
                        pstmt2.executeUpdate();
                        System.out.println("\nUser has been created successfully!\n");
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteCustomer(String ssn){
        String deleteAccount = "DELETE FROM account WHERE accountNumber = (SELECT accountNumber FROM user WHERE SSN = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteAccount)) {
            pstmt.setString(1, ssn);
            pstmt.executeUpdate();

            //Deleting user after deleting an account
            String deleteUser = "DELETE FROM user WHERE SSN = ?";
            try (PreparedStatement pstmt2 = conn.prepareStatement(deleteUser)) {
                pstmt2.setString(1, ssn);
                pstmt2.executeUpdate();
                System.out.println("\nUser has been deleted successfully!\n");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void makeDeposit(String ssn, double amount) throws SQLException {
        Customer customer = getCustomer(ssn);
        customer.getAccount().checkInterest(amount);
        amount= amount + amount * customer.getAccount().getInterest()/100;
        double currentBalance = customer.getAccount().getBalance() + amount;
        String deposit = "UPDATE account SET balance =  ?, interest = ? WHERE accountNumber = ?";
        try (PreparedStatement psmt = conn.prepareStatement(deposit)){
            psmt.setDouble(1, currentBalance);
            psmt.setDouble(2, customer.getAccount().getInterest()/100);
            psmt.setInt(3, customer.getAccount().getAccountNumber());
            psmt.executeUpdate();
            customer.getAccount().deposit(amount);
            System.out.println("\n" + customer.basicInfo() + " has been deposited.\n");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void makeWithdraw(String ssn, double amount) throws SQLException {
        Customer customer = getCustomer(ssn);
        if (customer.getAccount().withdraw(amount)) {
            String withdraw = "UPDATE account SET balance = ?, interest = ? WHERE accountNumber = ?";
            try (PreparedStatement psmt = conn.prepareStatement(withdraw)) {
                psmt.setDouble(1, customer.getAccount().getBalance());
                psmt.setDouble(2, customer.getAccount().getInterest());
                psmt.setInt(3, customer.getAccount().getAccountNumber());
                psmt.executeUpdate();
                System.out.println("\n" + customer.basicInfo() + " has been withdrawn.\n");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void resetPassword(String ssn) throws SQLException {
        String newPassword = RandomStringUtils.random(12, true, true);
        String reset = "UPDATE user SET password = ? WHERE SSN = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(reset)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, ssn);
            pstmt.executeUpdate();
            System.out.println("\nPassword has been reset to: " + newPassword + ".\n");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getUsers() throws SQLException {
        List<String> accList = new ArrayList<>();
        String prompt = "SELECT SSN, firstName, lastName, accountNumber FROM user WHERE SSN <> 'root' ORDER BY accountNumber ASC";
        try (PreparedStatement pstmt = conn.prepareStatement(prompt)){
            ResultSet rs = pstmt.executeQuery();

            int counter = 1;
            while (rs.next()) {
                System.out.println(
                        "\t" + counter + ") " +
                                rs.getString("firstName") + " "
                                + rs.getString("lastName") + " (" +
                                "Account number: " +
                                rs.getInt("accountNumber") + ")"
                );
                accList.add(rs.getString("SSN"));
                counter++;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return accList;
    }
}
