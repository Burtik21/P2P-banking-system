package P2PBank.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import P2PBank.utils.LoggerManager;


public class Server {
    private final ExecutorService executor;
    private final Thread serverThread;
    private ServerSocket serverSocket;
    private volatile boolean running = true; // Pro bezpečné vypnutí serveru

    public Server(int poolSize, int port) {
        executor = Executors.newFixedThreadPool(poolSize);

        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                String localIp = InetAddress.getLocalHost().getHostAddress();
                System.out.println("Server spuštěn na IP: " + localIp + " na portu: " + port);
                LoggerManager.info("Server spuštěn na IP: " + localIp + " na portu: " + port);

                while (running) {
                    try {
                        Socket socket = serverSocket.accept();
                        if (!running) break; // Pokud je vypnutý, ukončí cyklus

                        LoggerManager.info("Nové spojení: " + socket.getRemoteSocketAddress());
                        Handler handler = new Handler(socket);
                        executor.execute(handler);

                    } catch (IOException e) {
                        if (running) {

                            LoggerManager.error("Chyba při přijímání spojení",e);
                        }
                    }
                }

            } catch (IOException e) {
                LoggerManager.error("Chyba při spuštění serveru",e);
            } finally {
                stopServer();
            }
        });

        serverThread.setName("Server-Thread");
        serverThread.start();
    }

    public void stopServer() {
        running = false; // Zastaví hlavní smyčku přijímání klientů

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Zavře server socket
            }
        } catch (IOException e) {
            LoggerManager.error("Chyba při zavírání serverSocket",e);
        }

        executor.shutdownNow(); // Okamžitě zastaví všechny běžící thready
        //dispatched.clear(); // Vyprázdní mapu klientů
        LoggerManager.info("Server ukončen");
    }
}
