import java.util.ArrayList;

public class Bank {
    ArrayList<Customer> customers = new ArrayList<Customer>();
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public Customer getCustomer(int account) {
        return this.customers.get(account);
    }
    public ArrayList<Customer> getCustomers() {
        return this.customers;
    }
}
