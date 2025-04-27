public class Account {
    private double balance = 0;
    private double interest= 0.02;
    private int accountNumber = 0;

    Account(double balance, double interest, int accountNumber){
        this.balance = balance;
        this.interest = interest;
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return "Account information:\n"+
                "   Balance: " + balance + "\n" +
                "   Interest: " + interest + "\n" +
                "   Account number: " + accountNumber + "\n";
    }

    public double getInterest() {
        return interest * 100;
    }

    public double getBalance() {
        return balance;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public boolean withdraw(double amount) {
        if (amount + 5 > balance){
            System.out.println("You have insufficient funds.");
            return false;
        }
        balance -= (amount + 5);
        checkInterest(0);
        System.out.println("You have withdrawn $" + amount + " dollars and incurred a fee of $5." );
        System.out.println("You currently have a balance of $" + balance );
        return true;
    }

    public void deposit(double amount) {
        balance += amount;
        System.out.println("You have deposited $" + amount + " dollars with and interest rate of " + (interest*100) + "%.");
        System.out.println("You currently have a balance of $" + balance );
    }

    public void checkInterest(double amount){
        if (balance  + amount> 10000){
            interest = 0.05;
        }else{
            interest = 0.02;
        }
    }
}
