import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Menu {
    Scanner keyboard = new Scanner(System.in);
    Bank bank = new Bank();
    boolean exit = false;
    boolean admin = false;
    Customer customer = null;

    public Menu() throws SQLException {
    }

    public static void main(String[] args) throws InvalidAccountTypeException, SQLException {
        Menu menu = new Menu();
        menu.runMenu();
    }

    public void runMenu() throws InvalidAccountTypeException, SQLException {
        printHeader();
        System.out.println("1) Login");
        System.out.println("2) Exit");
        int choice = getInput(2);
        if (choice == 2) {displayHeader("Thank you for using our services!"); return;}

        boolean valid = false;
        while (!valid) {
            Pair<String, String> user = printLogin();
            String ssn = user.getKey();
            String password = user.getValue();
            String sql = "SELECT SSN, password, firstName, lastName, accountNumber FROM user WHERE SSN = ? AND password = ?";
            try (PreparedStatement stmt = bank.getConnection().prepareStatement(sql)) {
                stmt.setString(1, ssn);
                stmt.setString(2, password);

                try (ResultSet rs = stmt.executeQuery()){
                    if (rs.next()) {
                        //user logged
                        String userSSN = rs.getString("SSN");

                        if (userSSN.equals("root")){
                            admin = true;
                            displayHeader("Welcome in administration mode!");
                        }else{
                            customer = bank.getCustomer(userSSN);
                            displayHeader("Hello " + customer.getFirstName() + " " + customer.getLastName());
                        }
                        valid = true;
                    }else{
                        System.out.println("Invalid SSN or password. Try again.");
                    }
                }
            }
        }

        while(!exit){
            //menu while being logged
            if (admin){
                printAdminMenu();
                choice = getInput(8);
                performAdminAction(choice);
            }else{
                printUserMenu();
                choice = getInput(7);
            }
        }
    }

    private void performAdminAction(int choice) throws InvalidAccountTypeException, SQLException {
        switch (choice) {
            case 1:
                createNewCustomer();
                break;
            case 2:
                deleteCustomer();
                break;
            case 3:
                makeDeposit();
                break;
            case 4:
                makeWithdraw();
                break;
            case 5:
                //
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                exit = true;
                displayHeader("Looking forward to see you again root!");
                runMenu();
                break;
            default:
                System.out.println("Unknown error has occured.");
        }
    }

    private void makeDeposit() throws SQLException {
        displayHeader("Make a Deposit");
        int account = selectAccount();
        if (account > -1) {
            double amount = getAmount("How much would you like to deposit?");
            if (amount >= 0) {
                bank.makeDeposit(account, amount);
            }
            else{
                System.out.println("You cannot deposit negative amounts!");
            }
        }
    }

    private void deleteCustomer() {
        int choice = selectAccount();
        if (choice >-1){
            bank.deleteCustomer(choice);
        }
    }

    private void createNewCustomer() {
        displayHeader("Create an Account");
        //Get account information
        String firstName = askQuestion("Please enter your first name: ", null);
        String lastName = askQuestion("Please enter your last name: ", null);
        String ssn = askQuestion("Please enter your SSN: ", null);
        String password = askQuestion("Please enter your password: ", null);

        //Creating an account
        Account account = new Account(bank.getAccountID()+1);
        bank.incrementAccountID();
        Customer customer = new Customer(firstName, lastName, ssn, password, account);
        bank.addCustomer(customer);
    }

    private void printUserMenu() {
        displayHeader("User Menu");
        System.out.println("Please make a selection");
        System.out.println("1) Make a withdraw");
        System.out.println("2) Make a deposit");
        System.out.println("3) Make a transfer");
        System.out.println("4) Check account");
        System.out.println("5) Change password");
        System.out.println("6) Close an account");
        System.out.println("7) Logout");
    }

    private void printAdminMenu() {
        displayHeader("Admin Menu");
        System.out.println("Please make a selection");
        System.out.println("1) Create new customer account");
        System.out.println("2) Delete customer account");
        System.out.println("3) Make a deposit to customer");
        System.out.println("4) Make a withdraw from customer");
        System.out.println("5) List customer's personal data");
        System.out.println("6) List customer's account data");
        System.out.println("7) Reset customer's password");
        System.out.println("8) Logout");

    }

    private Pair<String, String> printLogin() {
        displayHeader("Login");
        String username = askQuestion("Please enter your social security number (SSN): ", null);
        String password = askQuestion("Please enter your password: ", null);
        return new Pair<>(username, password);
    }

    private int getInput(int max) {
        int choice = - 1;
        do {
            System.out.println("Please enter a your choice: ");
            try {
                choice = Integer.parseInt(keyboard.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid option. Try again.");
            }
            if (choice > max || choice < 1) {
                System.out.println("Choice is out of range. Please try again.");
            }
        }while (choice > max || choice < 1);
        return choice;
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

    private void printHeader() {
        System.out.println("#==============================================#");
        System.out.println("#            Welcome to Bank App               #");
        System.out.println("#       Best european bank of all time!        #");
        System.out.println("#==============================================#");
    }

    private int selectAccount() {
        ArrayList<Customer> customers = bank.getCustomers();
        if (customers.isEmpty()) {
            System.out.println("There are no customers at your bank.");
            return -1;
        }
        displayHeader("Select an account:");
        for (int i = 0; i < customers.size(); i++){
            System.out.println("\t" + (int)(i+1) + ") " + customers.get(i).basicInfo());
        }
        System.out.println("\t" + (int)(customers.size()+1) + ")  Exit");
        int account;
        System.out.println("Please make your selection: ");
        try {
            account = Integer.parseInt(keyboard.nextLine()) - 1;
        } catch (NumberFormatException e) {
            account = -1;
        }
        if (account < 0 || account > bank.getCustomers().size()) {
            System.out.println("Invalid account selected.");
            account = -1;
        }
        else if (account == bank.getCustomers().size()) {
            System.out.println("Exit...");
            return -1;
        }
        return account;
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

    private void makeWithdraw() throws SQLException {
        displayHeader("Make a Withdraw");
        int account = selectAccount();
        if (account > -1) {
            double amount = getAmount("How much would you like to withdraw?");
            if (amount >= 0) {
                bank.makeWithdraw(account, amount);
            }
            else{
                System.out.println("You cannot withdraw negative amounts!");
            }
        }
    }

}
