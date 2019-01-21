package de.uulm.in.vs.grn.chat.client.command.response;

public class PongResponse extends Response {
    public static final String COMMAND = "PONG";

    public PongResponse() {
        this.command = COMMAND;
    }

    public String[] getUsernames() {
        return fields.get("Usernames").split(",");
    }
}
