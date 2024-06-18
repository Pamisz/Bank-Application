import java.sql.*;
import java.util.ArrayList;

public class Bank {
    ArrayList<Customer> customers = new ArrayList<Customer>();
    String url = "jdbc:mysql://localhost/users";
    String databaseUsername = "pamisz";
    String databasePassword = "password";
    Connection conn = DriverManager.getConnection(url, databaseUsername, databasePassword);

    public Bank() throws SQLException {
        fetchCustomersFromDatabase();
    }

    private void fetchCustomersFromDatabase() throws SQLException {
        String sql = "SELECT SSN, password, firstName, lastName, accountNumber FROM user WHERE ssn != 'root'";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            String ssn = rs.getString("ssn");
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            int accountNumber = rs.getInt("accountNumber");

            String sql1 = "SELECT accountNumber, balance, interest FROM account WHERE accountNumber = ?";
            PreparedStatement stmt1 = conn.prepareStatement(sql1);
            stmt1.setInt(1, accountNumber);
            ResultSet rs1 = stmt1.executeQuery();
            while (rs1.next()) {
                double balance = rs1.getDouble("balance");
                double interest = rs1.getDouble("interest");

                Account account = new Account(balance, interest, accountNumber);
                Customer customer = new Customer(firstName, lastName, ssn, account);
                addCustomer(customer);
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

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void displayAllCustomers() {
        for (Customer customer : customers) {
            System.out.println(customer.toString());
        }
    }

}
