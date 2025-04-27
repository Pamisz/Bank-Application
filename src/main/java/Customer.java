public class Customer {

    private final String firstName;
    private final String lastName;
    private final String ssn;
    private String password;
    private final Account account;

    public Customer(String firstName, String lastName, String ssn, String password, Account account) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssn = ssn;
        this.account = account;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Customer information:\n"+
                "   First name: " + firstName + "\n" +
                "   Last name: " + lastName + "\n" +
                "   SSN: " + ssn + "\n" +
                "   Account number: " + account.getAccountNumber() + "\n";

    }

    public String basicInfo() {
        return " Account number: " + account.getAccountNumber() +
                " - Name: " + firstName + " " + lastName;
    }

    public Account getAccount() {
        return this.account;
    }

    public String getSSN(){
        return this.ssn;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String newPassword) {
        password = newPassword;
    }
}
