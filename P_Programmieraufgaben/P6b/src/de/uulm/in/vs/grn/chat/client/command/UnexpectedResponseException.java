package de.uulm.in.vs.grn.chat.client.command;

public class UnexpectedResponseException extends Exception {
    public UnexpectedResponseException() {
        super("Received valid response, but of unexpected type.");
    }

    public UnexpectedResponseException(String s) {
        super(s);
    }
}
