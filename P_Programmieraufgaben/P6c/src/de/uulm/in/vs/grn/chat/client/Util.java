package de.uulm.in.vs.grn.chat.client;

public class Util {

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
