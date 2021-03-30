package banking;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private User[] users = new User[1000];
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        DBConnection dbc = null;
        if (args[0].equals("-fileName") && args[1] != null) {
            dbc = new DBConnection(args[1]);
        } else {
            System.out.println("There is no file name of DB");
            System.exit(1);
        }
        BankSystem bs = new BankSystem(dbc.connect());
        while (true) {
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");
            String command = scanner.next();
            bs.startWork(command);
        }
    }
}