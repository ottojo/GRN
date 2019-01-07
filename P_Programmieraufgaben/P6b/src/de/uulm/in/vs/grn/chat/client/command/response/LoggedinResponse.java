package de.uulm.in.vs.grn.chat.client.command.response;

public class LoggedinResponse extends Response {
    public static final String COMMAND = "LOGGEDIN";

    public LoggedinResponse() {
        super();
        this.command = COMMAND;
    }
}
