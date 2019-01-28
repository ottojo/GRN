package de.uulm.in.vs.grn.chat.server;

public interface ChatMessageRelay {
    boolean sendMessage(String user, String chatMessage);
}
