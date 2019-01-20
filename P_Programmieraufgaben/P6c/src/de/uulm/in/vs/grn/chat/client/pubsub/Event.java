package de.uulm.in.vs.grn.chat.client.pubsub;

import de.uulm.in.vs.grn.chat.client.connection.InvalidMessageException;
import de.uulm.in.vs.grn.chat.client.connection.MissingFieldException;
import de.uulm.in.vs.grn.chat.client.connection.Message;

public class Event extends Message {

    private static final String COMMAND = "EVENT";

    public Event() {
        this.command = COMMAND;
    }

    public static Event parse(Message message) throws MissingFieldException, InvalidMessageException {
        if (!message.getCommand().equals(COMMAND)) {
            throw new InvalidMessageException("Not a valid event message: " + message);
        }
        Event e = new Event();
        e.fields = message.getAllFields();
        if (!e.fields.containsKey("Description")) {
            throw new MissingFieldException("Event does not contain description: " + message);
        }
        return e;
    }

    public String getDescription() {
        return fields.get("Description");
    }
}
