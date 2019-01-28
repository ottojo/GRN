package de.uulm.in.vs.grn.chat.server;

public class Main {
    public static void main(String[] args) {
        Debug.setPrintDebug(true);
        new GRNCPServer(1337, 1338).start();
    }
}
