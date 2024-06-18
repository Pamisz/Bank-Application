public class Checking extends Account{

    Checking(double initialDeposit){
        this.setBalance(initialDeposit);
        this.checkInterest(0);
    }

    @Override
    public String toString() {
        String accountType = "Checking";
        return "Account type: " + accountType + " Account\n" +
                "Account number: " + this.getAccountNumber() + "\n" +
                "Balance: " + this.getBalance() + "\n" +
                "Interest rate: " + this.getInterest() + "%\n";
    }
}
