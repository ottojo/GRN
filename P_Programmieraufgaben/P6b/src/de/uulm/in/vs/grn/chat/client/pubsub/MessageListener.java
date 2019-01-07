package de.uulm.in.vs.grn.chat.client.pubsub;

public interface MessageListener {
    void onChatMessage(ChatMessage chatMessage);
}
