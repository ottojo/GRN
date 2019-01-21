package de.uulm.in.vs.grn.chat.client.command.response;

public class UnknownResponseException extends Exception {
    public UnknownResponseException(String command) {
        super("Unknown command \"" + command + "\"");
    }
}
