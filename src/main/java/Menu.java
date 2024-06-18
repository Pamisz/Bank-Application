import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Menu {
    Scanner keyboard = new Scanner(System.in);
    Bank bank = new Bank();
    boolean exit = false;

    public static void main(String[] args) throws InvalidAccountTypeException {
        Menu menu = new Menu();
        menu.runMenu();
    }

    public void runMenu() throws InvalidAccountTypeException {
        printHeader();
        boolean valid = false;
        while (!valid) {
            Pair<String, String> user = printLogin();
            String ssn = user.getKey();
            String password = user.getValue();

        }
        while(!exit){
            printMenu();
            int choice = getInput();
            performAction(choice);
        }
    }

    private Pair<String, String> printLogin() {
        displayHeader("Login");
        String username = askQuestion("Please enter your social security number (SSN): ", null);
        String password = askQuestion("Please enter your password: ", null);
        return new Pair<>(username, password);
    }

    private int getInput() {
        int choice = - 1;
        do {
            System.out.println("Please enter a your choice: ");
            try {
                choice = Integer.parseInt(keyboard.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid option. Try again.");
            }
            if (choice > 5 || choice < 1) {
                System.out.println("Choice is out of range. Please try again.");
            }
        }while (choice > 5 || choice < 1);
        return choice;
    }

    private void performAction(int choice) throws InvalidAccountTypeException {
        switch (choice) {
            case 1:
                createAnAccount();
                break;
                case 2:
                    makeADeposit();
                    break;
                    case 3:
                        makeAWithdraw();
                        break;
                        case 4:
                            listBalance();
                            break;
                            case 5:
                                System.out.println("Thank you for using Bank App.");
                                System.exit(0);
                                break;
            default:
                System.out.println("Unknown error has occured.");
        }
    }

    private void listBalance() {
        displayHeader("List Account Details");
        int account = selectAccount();
        if (account >= 0) {
            displayHeader("Account Details");
            System.out.println(bank.getCustomer(account).getAccount());
        }
    }

    private void displayHeader(String header) {
        System.out.println();
        int width = header.length() + 6;
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        sb.append("-".repeat(Math.max(0, width)));
        sb.append("+");
        System.out.println(sb.toString());
        System.out.println("|   " + header + "   |");
        System.out.println(sb.toString());
    }

    private double getAmount(String question){
        System.out.println(question);
        double amount = 0;
        try {
            amount = Double.parseDouble(keyboard.nextLine());
        } catch (NumberFormatException e) {
            amount = 0;
        }
        return amount;
    }

    private void makeAWithdraw() {
        displayHeader("Make a Withdraw");
        int account = selectAccount();
        if (account >= 0) {
            double amount = getAmount("How much would you like to withdraw?");
            bank.getCustomer(account).getAccount().withdraw(amount);
        }
    }

    private void makeADeposit() {
        displayHeader("Make a Deposit");
        int account = selectAccount();
        if (account >= 0) {
            double amount = getAmount("How much would you like to deposit?");
            bank.getCustomer(account).getAccount().deposit(amount);
        }
    }

    private int selectAccount() {
        ArrayList<Customer> customers = bank.getCustomers();
        if (customers.isEmpty()) {
            System.out.println("There are no customers at your bank.");
            return -1;
        }
        System.out.println("Select an account:");
        for (int i = 0; i < customers.size(); i++){
            System.out.println("\t" + i+1 + ") " + customers.get(i).basicInfo());
        }
        int account;
        System.out.println("Please make your selection: ");
        try {
            account = Integer.parseInt(keyboard.nextLine()) - 1;
        } catch (NumberFormatException e) {
            account = -1;
        }
        if (account < 0 || account >= bank.getCustomers().size()) {
            System.out.println("Invalid account selected.");
            account = -1;
        }
        return account;
    }

    private String askQuestion(String question, List<String> answers){
        String response = "";
        Scanner input = new Scanner(System.in);
        boolean choices = answers != null && answers.size() != 0;
        boolean firstRun  = true;
        do {
            if (!firstRun){
                System.out.println("Invalid selection. Please try again.");
            }
            System.out.print(question);
            if (choices){
                System.out.print("(");
                for (int i = 0; i < answers.size() - 1; i++){
                    System.out.print(answers.get(i)+"/");
                }
                System.out.print(answers.getLast());
                System.out.print("): ");
            }
            response = input.nextLine();
            firstRun = false;
            if (!choices){
                break;
            }
        }while (!answers.contains(response));
        return response;
    }

    private double initialDeposit(String accountType){
        double initialDeposit = 0;
        boolean valid = false;
        while(!valid){
            System.out.println("Please enter an initial deposit: ");
            try {
                initialDeposit = Double.parseDouble(keyboard.nextLine());
            }
            catch (Exception e) {
                System.out.println("Invalid deposit value. Try again.");
            }
            if (accountType.equalsIgnoreCase("checking")){
                if (initialDeposit < 100){
                    System.out.println("Checking accounts require a minimum of 100$ to open.");
                }
                else{
                    valid = true;
                }
            }else {
                if (initialDeposit < 50){
                    System.out.println("Savings accounts require a minimum of 50$ to open.");
                }
                else{
                    valid = true;
                }
            }
        }
        return initialDeposit;
    }

    private void createAnAccount() throws InvalidAccountTypeException {
        displayHeader("Create an Account");
        //Get account information
        String accountType = askQuestion("Please enter an account type: ", Arrays.asList("checking", "savings"));
        String firstName = askQuestion("Please enter your first name: ", null);
        String lastName = askQuestion("Please enter your last name: ", null);
        String ssn = askQuestion("Please enter your SSN: ", null);
        double initialDeposit = initialDeposit(accountType);

        //Creating an account
        Account account;
        if (accountType.equalsIgnoreCase("checking")){
            account = new Checking(initialDeposit);
        } else if (accountType.equalsIgnoreCase("savings")){
            account = new Savings(initialDeposit);
        } else{
            throw new InvalidAccountTypeException();
        }
        Customer customer = new Customer(firstName, lastName, ssn, account);
        bank.addCustomer(customer);
    }

    private void printMenu() {
        displayHeader("Please make a selection");
        System.out.println("1) Create a new account");
        System.out.println("2) Make a deposit");
        System.out.println("3) Make a withdraw");
        System.out.println("4) List account balance");
        System.out.println("5) Exit");
    }

    private void printHeader() {
        System.out.println("#==============================================#");
        System.out.println("#            Welcome to Bank App               #");
        System.out.println("#       Best european bank of all time!        #");
        System.out.println("#==============================================#");
    }
}
