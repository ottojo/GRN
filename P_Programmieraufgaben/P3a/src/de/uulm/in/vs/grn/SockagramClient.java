package de.uulm.in.vs.grn;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SockagramClient {

    private static String SOCKAGRAM_HOST = "grn-services.lxd-vs.uni-ulm.de";
    private static int SOCKAGRAM_PORT = 7777;

    public static void main(String[] args) {

        parseArgs(args);

        try (
                Socket socket = new Socket(SOCKAGRAM_HOST, SOCKAGRAM_PORT);
                FileOutputStream fileOutputStream = new FileOutputStream("filter_" + args[0] + "_" + args[1])
        ) {
            byte[] imagePayload = new FileInputStream(args[1]).readAllBytes();
            SockagramRequest request = new SockagramRequest(Byte.parseByte(args[0]), imagePayload);
            socket.getOutputStream().write(request.makeRequest().array());
            fileOutputStream.write(SockagramResponse.parse(socket.getInputStream()));
        } catch (SockagramException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseArgs(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: filter_nr image_file [host [port]]");
            System.exit(1);
        }

        if (args.length >= 3) {
            SOCKAGRAM_HOST = args[2];
        }

        if (args.length >= 4) {
            try {
                SOCKAGRAM_PORT = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number.");
                System.exit(1);
            }
        }
    }
}
