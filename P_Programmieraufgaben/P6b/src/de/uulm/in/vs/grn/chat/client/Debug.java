package de.uulm.in.vs.grn.chat.client;

public class Debug {
    private static boolean printDebug = false;

    public static void println(Object o) {
        if (printDebug) System.err.println(o);
    }

    public static void enable() {
        printDebug = true;
    }

    public void disable() {
        printDebug = false;
    }
}
