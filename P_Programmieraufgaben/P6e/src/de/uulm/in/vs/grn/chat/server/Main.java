package de.uulm.in.vs.grn.chat.server;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: server <commandPort> <pubSubPort>");
            System.exit(0);
        }
        Debug.setPrintDebug(true);
        new GRNCPServer(Integer.parseInt(args[0]), Integer.parseInt(args[1])).start();
    }
}
