package P2PBank.database;

import P2PBank.resources.Config;
import P2PBank.utils.LoggerManager;

import java.sql.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DatabaseManager {
    private static final String DB_URL = Config.get("db.url");
    private static final String DB_USER = Config.get("db.user");
    private static final String DB_PASSWORD = Config.get("db.password");
    private static final String DB_DRIVER = Config.get("db.driver");

    static {
        try {
            Class.forName(DB_DRIVER);
            LoggerManager.info("JDBC driver načten.");
        } catch (ClassNotFoundException e) {
            LoggerManager.error("JDBC driver nebyl nalezen: " + DB_DRIVER, e);
            throw new RuntimeException("JDBC driver nebyl nalezen: " + DB_DRIVER);
        }
    }

    // ✅ Získání lokální IP adresy
    public static String getLocalIpAddress() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            LoggerManager.info("Získaná lokální IP adresa: " + ip);
            return ip;
        } catch (UnknownHostException e) {
            LoggerManager.error("Nelze zjistit IP adresu", e);
            return "0.0.0.0";
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // ✅ Vytvoření účtu (s IP banky)
    public static boolean createAccount(int accountNumber) {
        String bankIp = getLocalIpAddress();

        String sql = "INSERT INTO accounts (account_number, bank_ip, balance) VALUES (?, ?, 0)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountNumber);
            stmt.setString(2, bankIp);
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                LoggerManager.info("Účet vytvořen: " + accountNumber + "/" + bankIp);
            }
            return success;
        } catch (SQLException e) {
            LoggerManager.error("Chyba při vytváření účtu", e);
            return false;
        }
    }

    // ✅ Získání zůstatku účtu podle accountNumber/IP
    public static long getBalance(int accountNumber, String bankIp) {
        String sql = "SELECT balance FROM accounts WHERE account_number = ? AND bank_ip = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountNumber);
            stmt.setString(2, bankIp);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long balance = rs.getLong("balance");
                LoggerManager.info("Zůstatek pro účet " + accountNumber + "/" + bankIp + ": " + balance);
                return balance;
            }
        } catch (SQLException e) {
            LoggerManager.error("Chyba při načítání zůstatku", e);
        }
        return -1;
    }

    // ✅ Vklad na účet
    public static boolean deposit(int accountNumber, String bankIp, long amount) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ? AND bank_ip = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, amount);
            stmt.setInt(2, accountNumber);
            stmt.setString(3, bankIp);
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                LoggerManager.info("Vložen vklad: " + amount + " na účet " + accountNumber + "/" + bankIp);
            }
            return success;
        } catch (SQLException e) {
            LoggerManager.error("Chyba při vkladu", e);
            return false;
        }
    }

    // ✅ Výběr z účtu
    public static boolean withdraw(int accountNumber, String bankIp, long amount) {
        long currentBalance = getBalance(accountNumber, bankIp);
        if (currentBalance < amount) {
            LoggerManager.warn("Výběr z účtu " + accountNumber + "/" + bankIp + " zamítnut – nedostatek prostředků.");
            return false;
        }

        String sql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? AND bank_ip = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, amount);
            stmt.setInt(2, accountNumber);
            stmt.setString(3, bankIp);
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                LoggerManager.info("Vybráno " + amount + " z účtu " + accountNumber + "/" + bankIp);
            }
            return success;
        } catch (SQLException e) {
            LoggerManager.error("Chyba při výběru", e);
            return false;
        }
    }

    // ✅ Smazání účtu
    public static boolean removeAccount(int accountNumber, String bankIp) {
        if (getBalance(accountNumber, bankIp) != 0) {
            LoggerManager.warn("Smazání účtu " + accountNumber + "/" + bankIp + " zamítnuto – stále má zůstatek.");
            return false;
        }

        String sql = "DELETE FROM accounts WHERE account_number = ? AND bank_ip = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountNumber);
            stmt.setString(2, bankIp);
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                LoggerManager.info("Účet smazán: " + accountNumber + "/" + bankIp);
            }
            return success;
        } catch (SQLException e) {
            LoggerManager.error("Chyba při mazání účtu", e);
            return false;
        }
    }

    // ✅ Celková suma v bance
    public static long getTotalBalance() {
        String sql = "SELECT SUM(balance) FROM accounts";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                long total = rs.getLong(1);
                LoggerManager.info("Celková suma na účtech v bance: " + total);
                return total;
            }
        } catch (SQLException e) {
            LoggerManager.error("Chyba při načítání celkové sumy", e);
        }
        return 0;
    }

    // ✅ Počet klientů
    public static int getClientCount() {
        String sql = "SELECT COUNT(*) FROM accounts";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int count = rs.getInt(1);
                LoggerManager.info("Počet klientů v bance: " + count);
                return count;
            }
        } catch (SQLException e) {
            LoggerManager.error("Chyba při načítání počtu klientů", e);
        }
        return 0;
    }

    // ✅ Kontrola existence účtu
    public static boolean accountExists(int accountNumber) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                if (exists) {
                    LoggerManager.info("Účet existuje: " + accountNumber);
                }
                return exists;
            }
        } catch (SQLException e) {
            LoggerManager.error("Chyba při kontrole existence účtu", e);
        }
        return false;
    }
}
