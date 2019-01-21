package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.Debug;
import de.uulm.in.vs.grn.chat.client.pubsub.ChatMessage;
import de.uulm.in.vs.grn.chat.client.pubsub.Event;
import de.uulm.in.vs.grn.chat.client.pubsub.EventListener;
import de.uulm.in.vs.grn.chat.client.pubsub.MessageListener;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

public class PubSubConnection extends Connection {

    private LinkedList<MessageListener> messageListeners = new LinkedList<>();
    private LinkedList<EventListener> eventListeners = new LinkedList<>();

    public PubSubConnection(String host, int port) throws IOException {
        super(host, port);
    }

    @Override
    protected void onMessageFromServer(Message message) throws InvalidMessageException, MissingFieldException {

        Debug.println("[PubSub] Received: " + message);

        if (Objects.equals(message.getCommand(), "MESSAGE")) {
            ChatMessage chatMessage = ChatMessage.parse(message);
            messageListeners.forEach(messageListener -> messageListener.onChatMessage(chatMessage));
        } else if (Objects.equals(message.getCommand(), "EVENT")) {
            Event event = Event.parse(message);
            eventListeners.forEach((eventListener -> eventListener.onEvent(event)));
        } else {
            throw new InvalidMessageException("PubSub connection received message of unknown type: " + message);
        }
    }

    public void addOnMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    public void addOnEventListener(EventListener eventListener) {
        eventListeners.add(eventListener);
    }
}
