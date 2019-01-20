package de.uulm.in.vs.grn.chat.client.command.response;

public class ErrorResponse extends Response {
    public static final String COMMAND = "ERROR";

    public ErrorResponse() {
        command = COMMAND;
    }
}
