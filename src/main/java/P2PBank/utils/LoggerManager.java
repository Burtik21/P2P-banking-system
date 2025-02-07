package P2PBank.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerManager {
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = LOG_DIR + "/server.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        try {
            // ✅ Vytvoří složku `logs/`, pokud neexistuje
            File logDirectory = new File(LOG_DIR);
            if (!logDirectory.exists() && !logDirectory.mkdirs()) {
                throw new IOException("Nelze vytvořit logovací složku: " + LOG_DIR);
            }

            // ✅ Vytvoří soubor `server.log`, pokud neexistuje
            File logFile = new File(LOG_FILE);
            if (!logFile.exists() && !logFile.createNewFile()) {
                throw new IOException("Nelze vytvořit logovací soubor: " + LOG_FILE);
            }

        } catch (IOException e) {
            System.err.println("❌ Kritická chyba při inicializaci loggeru: " + e.getMessage());
        }
    }

    public static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logMessage = "[" + timestamp + "] [" + level + "] " + message;

        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(logMessage);
        } catch (IOException e) {
            System.err.println("❌ Chyba při zápisu do logu: " + e.getMessage());
        }
    }

    public static void info(String message) {
        log("INFO", message);
    }

    public static void warn(String message) {
        log("WARN", message);
    }

    public static void error(String message) {
        log("ERROR", message);
    }

    public static void error(String message, Throwable throwable) {
        log("ERROR", message + " - " + throwable.getMessage());
    }
}
