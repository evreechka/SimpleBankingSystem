package banking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BankSystem {
    private Random cardNumber = new Random();
    private Random pinCode = new Random();
    private Random idRandom = new Random();
    private List<User> users = new ArrayList<>();
    private Connection connection;

    public BankSystem(Connection connection) throws SQLException {
        this.connection = connection;
        try {
            // Statement execution
            createTable(connection.createStatement());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                "id INTEGER PRIMARY KEY," +
                "number TEXT NOT NULL," +
                "pin TEXT NOT NULL," +
                "balance INTEGER DEFAULT 0)");
    }

    protected boolean checkCommand(String command) {
        try {
            Integer.parseInt(command);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Invalid request :(");
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    protected void startWork(String command) throws SQLException {
        System.out.println(command);
        if (checkCommand(command)) {
            int option = Integer.parseInt(command);
            switch (option) {
                case 0:
                    close();
                    break;
                case 1:
                    createAccount();
                    break;
                case 2:
                    logIn();
                    break;
                default:
                    System.out.println("This command doesn't exist");
                    break;
            }
        }
    }

    protected void close() {
        System.out.println("Bye!");
        System.exit(1);
    }

    void createAccount() throws SQLException {
        Integer number1 = this.cardNumber.nextInt(100000);
        Integer number2 = this.cardNumber.nextInt(10000);
        Integer pinCode = this.pinCode.nextInt(10000);
        String cardNumber = "400000" + fixNum(number1) + fixPIN(number2) + generateLastDigit(4,number1, number2);
        String PIN = fixPIN(pinCode);
        Integer id = this.idRandom.nextInt(1000);
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(cardNumber);
        System.out.println("Your card PIN:");
        System.out.println(PIN);
        try(PreparedStatement ps = connection.prepareStatement("INSERT INTO card (id, number,pin, balance) VALUES (?, ?, ?, ?) ")) {
            ps.setInt(1, id);
            ps.setString(2, cardNumber);
            ps.setString(3, PIN);
            ps.setInt(4, 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void logIn() {
        boolean checker = false;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your card number:");
        String cardNumber = scanner.next();
        System.out.println("Enter your PIN:");
        String PIN = scanner.next();
        if (checkNumber(cardNumber) && checkPIN(PIN)) {
            try (Statement statement = connection.createStatement()){
                try(ResultSet rs = statement.executeQuery("SELECT * FROM card")) {
                    while (rs.next()) {
                        String currentNum = rs.getString("number");
                        String currentPIN = rs.getString("pin");
                        if (currentPIN.equals(PIN) && currentNum.equals(cardNumber)) {
                            checker = true;
                            System.out.println("You have successfully log in!");
                            new Account(rs.getInt("id"), connection).startWork();
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (!checker) {
//                    System.out.println("User with card number " + cardNumber + " and PIN " + PIN + " doesn't exist");
                System.out.println("Wrong card number or PIN!");
            }
        } else {
            System.out.println("Wrong card number or PIN!");
        }
//        if (checkNumber(cardNumber)){
//            System.out.println("Enter your PIN:");
//            String PIN = scanner.next();
//            if (checkPIN(PIN)) {
//                for (User user: users) {
//                    if (user.getPinCode().equals(PIN) && user.getCardNumber().equals(cardNumber)) {
//                        checker = true;
//                        System.out.println("You have successfully log in!");
//                        new Account(user).startWork();
//                        break;
//                    }
//                }
//                if (!checker) {
//                    System.out.println("User with card number " + cardNumber + " and PIN " + PIN + " doesn't exist");
//                }
//            }
//        }
    }

    private String fixPIN(Integer pinCode) {
//        System.out.println(pinCode.toString().length());
        switch (pinCode.toString().length()) {
            case 1:
                return "000" + pinCode.toString();
            case 2:
                return "00" + pinCode.toString();
            case 3:
                return "0" + pinCode.toString();
            default:
                return pinCode.toString();
        }
    }

    private String fixNum(Integer number) {
//        System.out.println(number.toString().length());
        switch (number.toString().length()) {
            case 1:
                return "0000" + number.toString();
            case 2:
                return "000" + number.toString();
            case 3:
                return "00" + number.toString();
            case 4:
                return "0" + number.toString();
            default:
                return number.toString();
        }
    }

    protected int generateLastDigit(int firstDigit, int number1, int number2) {
        int sum = 0;
        int[] array = new int[15];
        array[0] = firstDigit * 2;
        int id = 1;
        int count = 11;
        while (number1 != 0) {
            int digit = number1 % 10;
            if (count % 2 != 0) {
                array[id] = digit * 2;
                if (array[id] > 9) {
                    array[id] -= 9;
                }
            } else {
                array[id] = digit;
            }
            id++;
            count--;
            number1 /= 10;
        }
        count = 15;
        while (number2 != 0) {
            int digit = number2 % 10;
            if (count % 2 != 0) {
                array[id] = digit * 2;
                if (array[id] > 9) {
                    array[id] -= 9;
                }
            } else {
                array[id] = digit;
            }
            id++;
            count--;
            number2 /= 10;
        }
        for (int num : array) {
            sum += num;
        }
        int last = 10 - (sum % 10);
        if (last == 10) {
            last = 0;
        }
        return last;
    }

    protected boolean checkNumber(String number) {
        if (number.startsWith("400000") && number.matches("\\d+") && number.length() == 16) {
            return true;
        } else {
//            System.out.println("Invalid card number!");
            return false;
        }
    }

    private boolean checkPIN(String pin) {
        if (pin.matches("\\d+") && pin.length() == 4) {
            return true;
        } else {
//            System.out.println("Invalid PIN code!");
            return false;
        }
    }
}
