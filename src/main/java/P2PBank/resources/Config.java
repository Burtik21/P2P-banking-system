package P2PBank.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Nelze najít config.properties, Ujisti se, že je ve složce src/main/resources/");
            }
            properties.load(input);
            System.out.println("Konfigurace úspěšně načtena");
        } catch (IOException e) {
            System.err.println("Chyba při načítání konfigurace: " + e.getMessage());
            System.exit(1);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static int getInt(String key) {
        String value = get(key);
        if (value == null) {
            throw new NumberFormatException("❌ Chybí hodnota pro klíč: " + key);
        }
        return Integer.parseInt(value);
    }

    public static long getLong(String key) {
        String value = get(key);
        if (value == null) {
            throw new NumberFormatException("❌ Chybí hodnota pro klíč: " + key);
        }
        return Long.parseLong(value);
    }
}
