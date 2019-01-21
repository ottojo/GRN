package de.uulm.in.vs.grn.chat.client.pubsub;

import de.uulm.in.vs.grn.chat.client.connection.InvalidMessageException;
import de.uulm.in.vs.grn.chat.client.connection.MissingFieldException;
import de.uulm.in.vs.grn.chat.client.connection.Message;

public class ChatMessage extends Message {
    public static final String COMMAND = "MESSAGE";

    public ChatMessage() {
        this.command = COMMAND;
    }

    public static ChatMessage parse(Message message) throws MissingFieldException, InvalidMessageException {

        if (!message.getCommand().equals(COMMAND)) {
            throw new InvalidMessageException("Not a valid chat message: " + message);
        }

        ChatMessage c = new ChatMessage();

        c.fields = message.getAllFields();
        if (!c.fields.containsKey("Username")) {
            throw new MissingFieldException("Chat message is missing username.");
        }

        if (!c.fields.containsKey("Text")) {
            throw new MissingFieldException("Chat message is missing text.");
        }

        if (!c.fields.containsKey("Date")) {
            throw new MissingFieldException("Chat message is missing date.");
        }

        return c;
    }

    public String getUser() {
        return fields.get("Username");
    }

    public String getMessage() {
        return fields.get("Text");
    }

    public String getDate() {
        return fields.get("Date");
    }
}
