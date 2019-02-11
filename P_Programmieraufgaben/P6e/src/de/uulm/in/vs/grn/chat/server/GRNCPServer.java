package de.uulm.in.vs.grn.chat.server;

public class GRNCPServer implements ChatMessageRelay, MessageBroadcaster, EventRelay {
    private PubSubServer pubSubServer;
    private CommandServer commandServer;
    private int pubsubPort;
    private int commandPort;

    public GRNCPServer(int commandPort, int pubsubPort) {
        this.commandPort = commandPort;
        this.pubsubPort = pubsubPort;
    }

    public void start() {
        Debug.println("GRNCP Server starting");
        pubSubServer = new PubSubServer(pubsubPort);
        new Thread(pubSubServer).start();

        commandServer = new CommandServer(commandPort, this, this);
        new Thread(commandServer).start();
    }


    /**
     * Sends a Chat message to all clients via PubSub.
     *
     * @param user        Name of sending user
     * @param chatMessage Message
     * @return true if successful, false otherwise (not yet implemented)
     */
    @Override
    public boolean sendMessage(String user, String chatMessage) {
        Debug.println("GRNCP Server relaying message \"" + chatMessage + "\" from " + user + " to all clients.");
        Message message = new Message("MESSAGE");
        message.fields.put("Date", Util.dateFormat.format(new java.util.Date()));
        message.fields.put("Username", user);
        message.fields.put("Text", chatMessage);
        broadcastMessage(message);
        return true;
    }

    /**
     * Sends a message to all users via PubSub
     */
    @Override
    public void broadcastMessage(Message message) {
        pubSubServer.sendToAll(message);
    }

    @Override
    public void sendEvent(String description) {
        Message message = new Message("EVENT");
        message.fields.put("Date", Util.dateFormat.format(new java.util.Date()));
        message.fields.put("Description", description);
        broadcastMessage(message);
    }
}
