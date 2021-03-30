package banking;

import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    private String url = "jdbc:sqlite:";
    public DBConnection(String fileName) {
        this.url += fileName;
    }
    public Connection connect() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try {
            Connection connection = dataSource.getConnection();
            if (connection.isValid(5)) {
                return connection;
            } else {
                System.out.println("Не удалось подключиться к БД :(");
                System.exit(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
