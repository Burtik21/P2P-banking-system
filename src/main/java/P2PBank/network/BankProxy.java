package P2PBank.network;

import P2PBank.utils.LoggerManager;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class BankProxy {
    private static final List<Integer> PORTS = List.of(65525, 65526, 65527, 65528, 65529, 65530, 65531, 65532, 65533, 65534, 65535);
    private static final int TIMEOUT = 2000; // Timeout připojení v ms
    private static final Set<String> knownBanks = new HashSet<>(); // Cache nalezených bank

    public static String forwardCommand(String bankIp, String command) {
        LoggerManager.info("Proxy: Přesměrovávám příkaz [" + command + "] na " + bankIp);

        if (!knownBanks.contains(bankIp)) {
            LoggerManager.info("Proxy: IP adresa " + bankIp + " není v cache, skenuji síť...");
            scanForBanks(bankIp);
            if (!knownBanks.contains(bankIp)) {
                LoggerManager.warn("Proxy: Banka s IP " + bankIp + " nebyla nalezena v síti.");
                return "ER Banka nebyla nalezena v síti.";
            }
        }

        for (int port : PORTS) {
            try (Socket socket = new Socket(bankIp, port)) {
                socket.setSoTimeout(TIMEOUT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                LoggerManager.info("Připojeno k bance " + bankIp + ":" + port + ", posílám příkaz: " + command);
                writer.println(command);

                String response = reader.readLine();
                if (response != null) {
                    LoggerManager.info("Odpověď z banky " + bankIp + ":" + port + " → " + response);
                    return response;
                } else {
                    LoggerManager.warn("Banka na " + bankIp + ":" + port + " neodpověděla.");
                }

            } catch (SocketTimeoutException e) {
                LoggerManager.warn("Timeout při komunikaci s bankou " + bankIp + ":" + port);
            } catch (IOException e) {
                LoggerManager.warn("Nepodařilo se připojit na " + bankIp + ":" + port);
            }
        }

        return "ER Nepodařilo se připojit k bance " + bankIp;
    }

    public static void scanForBanks(String targetIp) {
        String baseIp = targetIp.substring(0, targetIp.lastIndexOf("."));
        LoggerManager.info("Skenuji síť v rozsahu: " + baseIp + ".0 - " + baseIp + ".255");

        List<String> foundBanks = NetworkScanner.scanNetwork(baseIp);

        if (foundBanks.isEmpty()) {
            LoggerManager.warn("Žádné banky v rozsahu " + baseIp + ".0 - " + baseIp + ".255 nebyly nalezeny.");
        } else {
            LoggerManager.info("Nalezené banky v síti: " + foundBanks);
            knownBanks.addAll(foundBanks);
        }
    }
}
