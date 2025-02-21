import java.sql.*;
import java.util.ArrayList;

public class Bank {
    private ArrayList<Customer> customers = new ArrayList<Customer>();
    private String url = "jdbc:mysql://localhost/users";
    private String databaseUsername = "pamisz";
    private String databasePassword = "password";
    private Connection conn = DriverManager.getConnection(url, databaseUsername, databasePassword);
    private int accountID = 0;

    public Bank() throws SQLException {
        fetchCustomersFromDatabase();
    }

    private void fetchCustomersFromDatabase() throws SQLException {
        String getCustomers = "SELECT SSN, password, firstName, lastName, accountNumber FROM user WHERE ssn != 'root'";
        PreparedStatement stmt = conn.prepareStatement(getCustomers);
        ResultSet rs = stmt.executeQuery(getCustomers);

        while (rs.next()) {
            String ssn = rs.getString("ssn");
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            String password = rs.getString("password");
            int accountNumber = rs.getInt("accountNumber");
            if (accountNumber > accountID) {
                accountID = accountNumber;
            }

            String getAccount = "SELECT accountNumber, balance, interest FROM account WHERE accountNumber = ?";
            PreparedStatement stmt1 = conn.prepareStatement(getAccount);
            stmt1.setInt(1, accountNumber);
            ResultSet rs1 = stmt1.executeQuery();
            while (rs1.next()) {
                double balance = rs1.getDouble("balance");
                double interest = rs1.getDouble("interest");

                Account account = new Account(balance, interest, accountNumber);
                Customer customer = new Customer(firstName, lastName, ssn, password, account);
                customers.add(customer);
            }
        }
    }

    public Customer getCustomer(String SSN) {
        for (Customer customer : customers) {
            if (customer.getSSN().equals(SSN)){
                return customer;
            }
        }
        return null;
    }

    public ArrayList<Customer> getCustomers() {
        return this.customers;
    }

    public Connection getConnection() {return this.conn;}

    public void addCustomer(Customer customer) {
        customers.add(customer);
        String sql = "INSERT INTO account (accountNumber, balance, interest) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customer.getAccount().getAccountNumber());
            pstmt.setDouble(2, customer.getAccount().getBalance());
            pstmt.setDouble(3, customer.getAccount().getInterest());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String insertUser = "INSERT INTO user (SSN, password, firstName, lastName, accountNumber) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertUser)) {
            pstmt.setString(1, customer.getSSN());
            pstmt.setString(2, customer.getPassword());
            pstmt.setString(3, customer.getFirstName());
            pstmt.setString(4, customer.getLastName());
            pstmt.setInt(5, customer.getAccount().getAccountNumber());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("\n" + customer.basicInfo() + " has been created.");
    }

    public void deleteCustomer(int number){
        Customer customer = customers.get(number);
        String deleteAccount = "DELETE FROM account WHERE accountNumber = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteAccount)) {
            pstmt.setInt(1, customer.getAccount().getAccountNumber());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String deleteUser = "DELETE FROM user WHERE SSN = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteUser)) {
            pstmt.setString(1, customer.getSSN());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("\n" + customer.basicInfo() + " has been deleted.");
        customers.remove(number);
    }

    public void makeDeposit(int number, double amount) throws SQLException {
        Customer customer = customers.get(number);
        customer.getAccount().checkInterest(amount);
        amount= amount + amount * customer.getAccount().getInterest()/100;
        double currentBalance = customer.getAccount().getBalance() + amount;
        String deposit = "UPDATE account SET balance =  ? WHERE accountNumber = ?";
        try (PreparedStatement psmt = conn.prepareStatement(deposit)){
            psmt.setDouble(1, currentBalance);
            psmt.setInt(2, customer.getAccount().getAccountNumber());
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        customer.getAccount().deposit(amount);
        System.out.println("\n" + customer.basicInfo() + " has been deposited.");
    }

    public void makeWithdraw(int number, double amount) throws SQLException {
        Customer customer = customers.get(number);
        if (customer.getAccount().withdraw(amount)) {
            amount = amount + 5;
            customer.getAccount().checkInterest(amount * -1);
            double currentBalance = customer.getAccount().getBalance() - amount;
            String withdraw = "UPDATE account SET balance = ? WHERE accountNumber = ?";
            try (PreparedStatement psmt = conn.prepareStatement(withdraw)) {
                psmt.setDouble(1, currentBalance);
                psmt.setInt(2, customer.getAccount().getAccountNumber());
                psmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println("\n" + customer.basicInfo() + " has been withdrawn.");
        }
    }

    public void displayAllCustomers() {
        for (Customer customer : customers) {
            System.out.println(customer.toString());
        }
    }

    public int getAccountID(){
        return accountID;
    }

    public void incrementAccountID(){
        accountID++;
    }


}
