package de.uulm.in.vs.grn.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

public class CommandClient implements Runnable {
    private Socket clientSocket;
    private CommandMessageHandler server;

    public CommandClient(Socket clientSocket, CommandMessageHandler server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        Debug.println("CommandClient starting to process incoming data");
        try (BufferedReader socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            while (!clientSocket.isClosed()) {
                StringBuilder contents = new StringBuilder();
                String l = socketReader.readLine();
                if (l == null) break;

                while (l != null && !l.equals("")) {
                    contents.append(l);
                    contents.append("\r\n");
                    l = socketReader.readLine();
                }

                Message message;
                try {
                    message = Message.parse(contents.toString());
                    Debug.println("Comand client parsed message: " + message.toString());
                    server.handleMessage(this, message);
                } catch (UnknownProtocolException e) {
                    e.printStackTrace();
                    Message error = new Message("ERROR");
                    error.fields.put("Date", Util.dateFormat.format(new Date()));
                    error.fields.put("Reason", "Unknown Protocol Version");
                    sendMessage(error);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            clientSocket.getOutputStream().write(message.build());
            clientSocket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
