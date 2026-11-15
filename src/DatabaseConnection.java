import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "";
    private static final String USER = "root";      // MySQL kullanıcı adın
    private static final String PASSWORD = "@";      // MySQL şifren

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}