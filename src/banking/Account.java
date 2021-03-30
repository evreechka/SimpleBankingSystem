package banking;

import java.sql.*;
import java.util.Scanner;

public class Account extends BankSystem{
    private int currentUser;
    private int idTransfer;
    private Scanner scanner = new Scanner(System.in);
    private Connection connection;
    public Account(int id, Connection connection) throws SQLException {
        super(connection);
        this.connection = connection;
        this.currentUser = id;
    }
    public void startWork() throws SQLException {
        while (true) {
            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");
            String command = scanner.next();
            if (checkCommand(command)) {
                int option = Integer.parseInt(command);
                switch (option) {
                    case 0:
                        close();
                        break;
                    case 1:
                        System.out.println("Balance: " + getBalance());
                        break;
                    case 2:
                        addIncome(currentUser);
                        break;
                    case 3:
                        doTransfer();
                        break;
                    case 4:
                        if (closeAccount()) {
                          return;
                        }
                        break;
                    case 5:
                        System.out.println("You have successfully log out!");
                        return;
                }
            }
        }
    }
    private int getBalance() throws SQLException {
        try(Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery("SELECT * FROM card WHERE id = '" + currentUser + "'")) {
                return rs.getInt("balance");
            }
        }
    }
    private void addIncome(int currentUser) {
        System.out.println("Enter income:");
        int income = scanner.nextInt();
        String statement = "UPDATE card SET balance = balance + ? WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(statement)) {
            ps.setInt(1, income);
            ps.setInt(2, currentUser);
            ps.executeUpdate();
            System.out.println("Income was added!");
        } catch (SQLException e) {
            System.out.println("There is some troubles. Please try later");
        }
    }
    private void doTransfer() throws SQLException {
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String cardNumber = scanner.next();
        if (checkCardNumber(cardNumber)) {
            System.out.println("Enter how much money you want to transfer:");
            int transfer = scanner.nextInt();
            if (checkBalance(transfer)) {
                try (PreparedStatement ps = connection.prepareStatement("UPDATE card SET balance = balance - ? WHERE id = ?")) {
                    ps.setInt(1, transfer);
                    ps.setInt(2, currentUser);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = connection.prepareStatement("UPDATE card SET balance = balance + ? WHERE id = ?")) {
                    ps.setInt(1, transfer);
                    ps.setInt(2, idTransfer);
                    ps.executeUpdate();
                }
                System.out.println("Success!");
            } else {
                System.out.println("Not enough money!");
            }
        }

    }
    private boolean checkCardNumber(String cardNumber) throws SQLException {
//        if (!checkNumber(cardNumber)) {
//            System.out.println("Probably you made mistake in the card number. Please try again");
//            return false;
//        } else
        if (!checkLastDigit(cardNumber)){
            System.out.println("Probably you made mistake in the card number. Please try again");
            return false;
        } else {
            return checkExisting(cardNumber);
        }
    }
    private boolean checkLastDigit (String cardNumber) {
        int firstDigit = Integer.parseInt(cardNumber.substring(0,1));
        int number1 = Integer.parseInt(cardNumber.substring(6, 11));
        int number2 = Integer.parseInt(cardNumber.substring(11, 15));
        int lastNum = Integer.parseInt(cardNumber.substring(15));
        return generateLastDigit(firstDigit, number1, number2) == lastNum;
    }
    private boolean checkExisting(String cardNumber) throws SQLException {
        Integer id = null;
        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery("SELECT id FROM card WHERE number = " + cardNumber)) {
                if (rs.next()) {
                    id = rs.getInt("id");
                }
            }
        }
        if (id == null) {
            System.out.println("Such card does not exist.");
            return false;
        } else {
            idTransfer = id;
            return true;
        }
    }
    private boolean checkBalance(int transfer) throws SQLException {
        int balance = 0;
        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery("SELECT balance FROM card WHERE id = " + currentUser)) {
                balance = rs.getInt("balance");
            }
        }
        return balance >= transfer;
    }
    private boolean closeAccount() {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM card WHERE id = ?")){
            ps.setInt(1,currentUser);
            ps.executeUpdate();
            System.out.println("The account has been closed!");
            return true;
        } catch (SQLException e) {
            System.out.println("There is some troubles. Please try later");
            return false;
        }
    }
}
