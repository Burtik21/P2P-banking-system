package P2PBank.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService executor;
    private final Thread serverThread;
    private final HashMap<String, Handler> dispatched = new HashMap<>();

    public Server(int poolSize, int port) {
        executor = Executors.newFixedThreadPool(poolSize);
        serverThread = new Thread(() -> new Listener(this, port).listen());
        serverThread.setName("server");
        serverThread.start();
        System.out.println("Server spuštěn na portu " + port);
    }

    public void handle(Socket clientSocket) {
        try {
            Handler h = new Handler(clientSocket);
            executor.execute(h);
            dispatched.put(h.getName(), h);
            System.out.println("Připojen nový klient: " + h.getName());
        } catch (IOException e) {
            System.err.println("Chyba při zpracování klienta: " + e.getMessage());
        }
    }

    public void shutdownExecutor() {
        executor.shutdownNow();
        System.out.println("Server zastaven.");
    }

    public HashMap<String, Handler> getHandlers() {
        return dispatched;
    }

    public void releaseHandler(String name) {
        dispatched.remove(name);
        System.out.println("Klient " + name + " odpojen.");
    }

    public Thread getServerThread() {
        return serverThread;
    }

    public static void main(String[] args) {
        new Server(10, 65525); // 10 vláken, port 65525
    }
}
