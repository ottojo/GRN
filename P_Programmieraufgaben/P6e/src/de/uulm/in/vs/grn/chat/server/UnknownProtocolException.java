package de.uulm.in.vs.grn.chat.server;

public class UnknownProtocolException extends Exception {
    public UnknownProtocolException(String version) {
        super("Unknown protocol version \"" + version + "\"");
    }
}