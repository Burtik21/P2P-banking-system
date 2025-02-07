package P2PBank;

import P2PBank.database.DatabaseManager;
import P2PBank.resources.Config;
import P2PBank.server.Server;
import P2PBank.utils.LoggerManager;

import java.sql.Connection;
import java.sql.SQLException;

public class Application {

    public void start() {
        LoggerManager.info("Spouštím P2P Banking System...");

        // ✅ Načtení konfigurace
        loadConfig();

        // ✅ Test připojení k databázi
        testDatabaseConnection();

        // ✅ Spuštění serveru
        startServer();
    }

    private void loadConfig() {
        LoggerManager.info("Načítám konfiguraci...");
        LoggerManager.info("Banka: " + Config.get("server.bankName"));
        LoggerManager.info("Port: " + Config.getInt("server.port"));
    }

    private void testDatabaseConnection() {
        LoggerManager.info("Testuji připojení k databázi...");
        try (Connection conn = DatabaseManager.getConnection()) {
            LoggerManager.info("Připojení k databázi OK!");
        } catch (SQLException e) {
            LoggerManager.error("Chyba připojení k databázi", e);
            System.exit(1);
        }
    }

    private void startServer() {
        LoggerManager.info("Spouštím server...");
        try {
            int poolSize = Config.getInt("server.threadPoolSize");
            int port = Config.getInt("server.port");
            new Server(poolSize, port);
        } catch (Exception e) {
            LoggerManager.error("Chyba při spouštění serveru", e);
            System.exit(1);
        }
    }
}
