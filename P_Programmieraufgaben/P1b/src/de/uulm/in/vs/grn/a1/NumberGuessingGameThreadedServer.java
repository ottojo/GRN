package de.uulm.in.vs.grn.a1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NumberGuessingGameThreadedServer {
    private static final int PORT = 5555;

    public static void main(String[] args) {
        try {
            ServerSocket guessingServer = new ServerSocket(PORT);
            ExecutorService executorService = Executors.newFixedThreadPool(4);

            while (!guessingServer.isClosed()) {
                Socket client = guessingServer.accept();
                executorService.execute(new NumberGuessingGameRequestHandler(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
