public class Savings extends Account{

    Savings(double initialDeposit){
        this.setBalance(initialDeposit);
        this.checkInterest(0);
    }

    @Override
    public String toString() {
        String accountType = "Savings";
        return "Account type: " + accountType + " Account\n" +
                "Account number: " + this.getAccountNumber() + "\n" +
                "Balance: " + this.getBalance() + "\n" +
                "Interest rate: " + this.getInterest() + "%\n";
    }
}
