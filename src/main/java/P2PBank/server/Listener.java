package P2PBank.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener {
    private ServerSocket serverSocket;
    private Server server;

    public Listener(Server server, int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.server = server;
            System.out.println("Listener běží na portu " + port);
        } catch (IOException e) {
            System.err.println("Chyba při spuštění Listeneru: " + e.getMessage());
        }
    }

    public void listen() {
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Přijaté spojení: " + clientSocket.getRemoteSocketAddress());
                server.handle(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Chyba Listeneru: " + e.getMessage());
        }
    }

    public void closeListener() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                System.out.println("Listener uzavřen.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
