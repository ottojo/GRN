package de.uulm.in.vs.grn.chat.server;

public interface CommandMessageHandler {
    void handleMessage(CommandClient client, Message message);
}
