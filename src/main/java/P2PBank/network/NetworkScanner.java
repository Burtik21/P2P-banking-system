package P2PBank.network;

import P2PBank.resources.Config;
import P2PBank.utils.LoggerManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class NetworkScanner {
    private static final int THREAD_COUNT = 20;
    private static final int TIMEOUT;

    static {
        int timeout;
        try {
            timeout = Config.getInt("network.scanTimeout");
        } catch (NumberFormatException e) {
            LoggerManager.warn("Chyba: network.scanTimeout není nastaven! Používám výchozí hodnotu 1000 ms.");
            timeout = 1000; // Výchozí hodnota, pokud chybí
        }
        TIMEOUT = timeout;
    }

    public static List<String> scanNetwork(String baseIp) {
        List<String> activeBanks = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> futures = new ArrayList<>();

        LoggerManager.info("Skenuji síť v rozsahu: " + baseIp + ".0 - " + baseIp + ".255");

        for (int i = 0; i <= 255; i++) {
            String ip = baseIp + "." + i;
            futures.add(executor.submit(() -> checkBankServer(ip) ? ip : null));
        }

        executor.shutdown();
        for (Future<String> future : futures) {
            try {
                String ip = future.get();
                if (ip != null) activeBanks.add(ip);
            } catch (Exception ignored) {}
        }

        if (activeBanks.isEmpty()) {
            LoggerManager.warn("Žádné banky v rozsahu " + baseIp + ".0 - " + baseIp + ".255 nebyly nalezeny.");
        } else {
            LoggerManager.info("Nalezené banky: " + activeBanks);
        }

        return activeBanks;
    }

    /**
     * Ověří, zda na IP běží bankovní server a odpovídá na příkaz BC.
     */
    private static boolean checkBankServer(String ip) {
        if (!ping(ip)) return false; // Pokud IP neodpovídá na ping, ignorujeme ji.

        for (int port : List.of(65525, 65526, 65527, 65528, 65529, 65530, 65531, 65532, 65533, 65534, 65535)) {
            try (Socket socket = new Socket(ip, port)) {
                socket.setSoTimeout(TIMEOUT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                //  Pošleme testovací příkaz BC
                writer.println("BC");
                String response = reader.readLine();

                if (response != null && response.startsWith("BC ")) {
                    LoggerManager.info("Banka nalezena na " + ip + ":" + port + " s odpovědí: " + response);
                    return true;
                } else {
                    LoggerManager.warn("Na " + ip + ":" + port + " běží něco, ale ne banka.");
                }
            } catch (IOException ignored) {}
        }
        return false;
    }

    private static boolean ping(String ip) {
        try {
            return InetAddress.getByName(ip).isReachable(TIMEOUT);
        } catch (Exception e) {
            return false;
        }
    }
}
