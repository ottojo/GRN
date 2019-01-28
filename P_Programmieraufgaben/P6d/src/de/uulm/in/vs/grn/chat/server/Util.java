package de.uulm.in.vs.grn.chat.server;

import java.text.SimpleDateFormat;

public class Util {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");


    /**
     * @return true if string only contains of a-z, A-Z. 0-9
     */
    static boolean isBasicASCII(String s) {
        for (char c : s.toCharArray()) {
            if (c < '0') return false;
            if (c > 'z') return false;
            if (c > '9' && c < 'A') return false;
            if (c > 'Z' && c < 'a') return false;
        }
        return true;
    }
}