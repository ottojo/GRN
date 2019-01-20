package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.Debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Objects;

/**
 * Generic connection for sending and receiving {@link Message}s.
 */
public abstract class Connection implements Runnable {
    private Socket socket;

    protected Connection(String host, int port) throws IOException {
        Debug.println("Connection initializing (" + host + ":" + port + ")");
        socket = new Socket(host, port);
        Debug.println("Connected to " + host + " on port " + port);

    }

    @Override
    public void run() {
        Debug.println("Connection starting to process incoming data");
        try (BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            while (!socket.isClosed()) {
                StringBuilder contents = new StringBuilder();
                String l;
                while (!Objects.equals(l = socketReader.readLine(), "")) {
                    contents.append(l);
                    contents.append("\r\n");
                }

                Message message;
                try {
                    message = Message.parse(contents.toString());
                    onMessageFromServer(message);
                } catch (UnknownProtocolException | InvalidMessageException | MissingFieldException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void sendMessage(Message message) throws IOException {
        socket.getOutputStream().write(message.build());
    }

    protected abstract void onMessageFromServer(Message message) throws InvalidMessageException, MissingFieldException;
}
