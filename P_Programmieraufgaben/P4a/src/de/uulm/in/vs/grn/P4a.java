package de.uulm.in.vs.grn;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class P4a {
    private static final int PORT = 7777;
    private static Map<String, String> storage = new HashMap<>();

    public static void main(String[] args) {

        ServerSocket server = null;

        try {
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Could not open server socket.");
            e.printStackTrace();
            System.exit(1);
        }

        while (!server.isClosed()) {
            System.out.println("Waiting for client");
            try (Socket client = server.accept()) {

                BufferedReader clientReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                while (!client.isClosed()) {
                    String command = clientReader.readLine();
                    if (command == null) {
                        System.out.println("END OF STREAM");
                        break;
                    }
                    command = command.trim();
                    String[] commandArgs = command.split(" ");

                    switch (commandArgs[0]) {
                        case "PUT":
                            if (commandArgs.length < 3) {
                                clientWriter.write("ERR: Not enough arguments\n");
                                clientWriter.flush();
                            }
                            storage.put(commandArgs[1], commandArgs[2]);
                            clientWriter.write("RES: OK\n");
                            clientWriter.flush();
                            break;
                        case "GET":
                            if (commandArgs.length < 2) {
                                clientWriter.write("ERR: Not enough arguments\n");
                                clientWriter.flush();
                            }
                            String result = storage.get(commandArgs[1]);
                            if (result != null) {
                                clientWriter.write("RES: " + result + "\n");
                                clientWriter.flush();
                            } else {
                                clientWriter.write("ERR: Unknown Key!\n");
                                clientWriter.flush();
                            }
                            break;
                        case "EXIT":
                            clientWriter.write("RES: BYE!\n");
                            clientWriter.flush();
                            client.close();
                            break;
                        default:
                            clientWriter.write("ERR: Unknown Command!\n");
                            clientWriter.flush();
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
