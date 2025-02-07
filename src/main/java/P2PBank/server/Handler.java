package P2PBank.server;

import P2PBank.commands.Command;
import P2PBank.commands.CommandHolder;
import P2PBank.resources.Config;
import P2PBank.utils.LoggerManager;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Handler implements Runnable {
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    public Handler(Socket socket) throws IOException {
        this.socket = socket;
        this.socket.setSoTimeout(Config.getInt("server.idleTimeout"));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

        LoggerManager.info("Nové spojení od: " + socket.getRemoteSocketAddress());
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            String instruction;
            while ((instruction = reader.readLine()) != null) {
                instruction = instruction.replace("\r", "").trim();
                if (instruction.isEmpty()) continue;

                LoggerManager.info("Přijatý příkaz: " + instruction);

                // Zpracování příkazu
                String response = processCommand(instruction);
                writer.println(response);
                LoggerManager.info("Odpověď odeslána: " + response);
            }

        } catch (SocketException e) {
            if (e.getMessage().equals("Connection reset")) {
                LoggerManager.warn("Klient se nečekaně odpojil (PuTTY zavřeno).");
            } else {
                LoggerManager.error("Chyba socketu: " + e.getMessage(), e);
            }
        } catch (IOException e) {
            LoggerManager.error("Klient se odpojil nebo nastala chyba: " + e.getMessage(), e);
        } finally {
            closeEverything();
        }
    }

    private String processCommand(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 0) return "ER Neplatný příkaz.";

        String commandKey = parts[0].toUpperCase();
        Command command = CommandHolder.getCommand(commandKey);
        if (command != null) {
            return command.execute(parts);
        }
        return "ER Neznámý příkaz.";
    }

    private void closeEverything() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            LoggerManager.error("Chyba při zavírání socketu: " + e.getMessage(), e);
        }
        LoggerManager.info("Klient " + socket.getRemoteSocketAddress() + " spojení ukončil.");
    }

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message + "\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            LoggerManager.error("Chyba při odesílání zprávy: " + e.getMessage(), e);
            closeEverything();
        }
    }
}
