package P2PBank.database;

import P2PBank.config.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DB_URL = Config.get("db.url");
    private static final String DB_USER = Config.get("db.user");
    private static final String DB_PASSWORD = Config.get("db.password");
    private static final String DB_DRIVER = Config.get("db.driver");

    static {
        try {
            Class.forName(DB_DRIVER); // Načtení JDBC driveru
            System.out.println("✅ JDBC driver načten.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("❌ JDBC driver nebyl nalezen: " + DB_DRIVER);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
