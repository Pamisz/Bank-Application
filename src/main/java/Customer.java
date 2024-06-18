public class Customer {

    private final String firstName;
    private final String lastName;
    private final String ssn;
    private final Account account;

    public Customer(String firstName, String lastName, String ssn, Account account) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssn = ssn;
        this.account = account;
    }

    @Override
    public String toString() {
        return "Customer information:\n"+
                "First name: " + firstName + "\n" +
                "Last name: " + lastName + "\n" +
                "SSN: " + ssn + "\n" +
                account.toString();

    }

    public String basicInfo() {
        return " Account number: " + account.getAccountNumber() +
                " - Name: " + firstName + " " + lastName;
    }

    public Account getAccount() {
        return this.account;
    }
}
