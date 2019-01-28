package de.uulm.in.vs.grn.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommandServer implements Runnable, CommandMessageHandler {
    private ChatMessageRelay chatMessageRelay;
    private EventRelay eventRelay;
    private Map<CommandClient, String> clients;
    private int port;
    private ServerSocket serverSocket;

    public CommandServer(int port, ChatMessageRelay chatMessageRelay, EventRelay eventRelay) {
        this.chatMessageRelay = chatMessageRelay;
        this.eventRelay = eventRelay;
        this.port = port;
        clients = new HashMap<>();

    }


    @Override
    public void run() {

        Debug.println("Command Server listening for clients");
        try {
            serverSocket = new ServerSocket(port);
            while (!serverSocket.isClosed()) {
                CommandClient c = new CommandClient(serverSocket.accept(), this);
                new Thread(c).start();
                clients.put(c, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(CommandClient client, Message message) {
        switch (message.command) {
            //TODO reply to PING
            case "SEND": {
                boolean successful = true;
                // Client wants to send message
                String errorString = "";
                if (!clients.containsKey(client)) {
                    Debug.println("Command Server Received Message from unknown user");
                    successful = false;
                }

                if (clients.get(client) == null) {
                    Debug.println("Command Server Received Message from user that has not logged in yet");
                    successful = false;
                }

                if (message.fields.get("Text").getBytes().length > 512) {
                    Debug.println("Command Server Received Message that is too long");
                    errorString = "Message too long.";
                    successful = false;
                }

                // Relay Chat message to GRNCPServer for distribution via PubSub
                if (successful) {
                    successful = chatMessageRelay.sendMessage(clients.get(client), message.fields.get("Text"));
                }

                if (successful) {
                    Debug.println("Command Server Successful chat message relay, sending SENT");
                    Message successmessage = new Message("SENT");
                    successmessage.fields.put("Date", Util.dateFormat.format(new Date()));
                    client.sendMessage(successmessage);
                } else {
                    Message errorMessage = new Message("ERROR");
                    errorMessage.fields.put("Date", Util.dateFormat.format(new Date()));
                    errorMessage.fields.put("Reason", errorString);
                    client.sendMessage(errorMessage);
                }

                break;
            }
            case "LOGIN": {
                try {
                    loginUser(client, message.fields.get("Username"));
                    Debug.println("Command Server Successful Login, sending LOGGEDIN");
                    Message successMessage = new Message("LOGGEDIN");
                    successMessage.fields.put("Date", Util.dateFormat.format(new Date()));
                    client.sendMessage(successMessage);
                } catch (InvalidUsernameException e) {
                    Debug.println("Command Server Error logging in, sending ERROR");
                    Message errorMessage = new Message("ERROR");
                    errorMessage.fields.put("Date", Util.dateFormat.format(new Date()));
                    errorMessage.fields.put("Reason", e.getMessage());
                    client.sendMessage(errorMessage);
                }
                break;
            }
            case "BYE": {

                if (clients.containsKey(client)) {
                    Message byebyeMessage = new Message("BYEBYE");
                    byebyeMessage.fields.put("Date", Util.dateFormat.format(new Date()));

                    client.sendMessage(byebyeMessage);

                    eventRelay.sendEvent(clients.get(client) + " has left");

                    clients.remove(client);

                } else {
                    //TODO logout failure
                }

                break;
            }
            default: {
                Message errorMessage = new Message("ERROR");
                errorMessage.fields.put("Date", Util.dateFormat.format(new Date()));
                errorMessage.fields.put("Reason", "Unknown Command");
                client.sendMessage(errorMessage);
            }
        }
    }

    /**
     * Tries to log in the specified user
     * Fails if username is invalid or user is already logged in.
     *
     * @param username will be validated
     */
    public void loginUser(CommandClient client, String username) throws InvalidUsernameException {

        if (username.length() < 3) {
            Debug.println("Command Server Username \"" + username + "\" too short");
            throw new InvalidUsernameException("Username too short");
        }
        if (username.length() > 15) {
            Debug.println("Command Server Username \"" + username + "\" too long");
            throw new InvalidUsernameException("Username too long");
        }
        if (!Util.isBasicASCII(username)) {
            Debug.println("Command Server Username \"" + username + "\" contains invalid characters.");
            throw new InvalidUsernameException("Username contains invalid characters.");
        }
        if (clients.containsValue(username)) {
            Debug.println("Command Server Username \"" + username + "\" is already logged in.");
            throw new InvalidUsernameException("The selected username is already in use.");
        }

        clients.put(client, username);

        eventRelay.sendEvent(username + " has joined");
    }

}
