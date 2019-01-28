package de.uulm.in.vs.grn.chat.server;

public class Debug {
    private static boolean printDebug = false;

    public static void println(Object o) {
        if (printDebug) System.out.println(o);
    }

    public static void setPrintDebug(boolean printDebug) {
        Debug.printDebug = printDebug;
    }
}
