package P2PBank.server;

import P2PBank.commands.Command;
import P2PBank.commands.CommandHolder;

import java.io.*;
import java.net.Socket;

public class Handler implements Runnable {
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    public Handler(Socket socket) throws IOException {
        this.socket = socket;
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    }

    @Override
    public void run() {
        String messageFromClient;
        try {
            while (socket.isConnected() && (messageFromClient = bufferedReader.readLine()) != null) {
                String response = processCommand(messageFromClient);
                sendMessage(response);
            }
        } catch (IOException e) {
            System.out.println("Klient odpojen.");
        } finally {
            closeEverything();
        }
    }

    private String processCommand(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 0) return "ER Neplatný příkaz.";

        Command command = CommandHolder.getCommand(parts[0]);
        if (command != null) {
            return command.execute(parts);
        }
        return "ER Neznámý příkaz.";
    }
    private void closeEverything() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Klient spojení ukončil.");
    }


    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message + "\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }
}
