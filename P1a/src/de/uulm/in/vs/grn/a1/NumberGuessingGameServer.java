package de.uulm.in.vs.grn.a1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ThreadLocalRandom;

public class NumberGuessingGameServer {
    private static final int PORT = 5555;

    public static void main(String[] args) {
        try {
            ServerSocket guessingServer = new ServerSocket(PORT);

            while (!guessingServer.isClosed()) {

                Socket client = guessingServer.accept();
                System.out.printf("Client %s connected.\n", client.getInetAddress().toString());

                try {
                    InputStream inputStream = client.getInputStream();
                    OutputStream outputStream = client.getOutputStream();

                    // Game init
                    int number = ThreadLocalRandom.current().nextInt(50);
                    System.out.printf("Number for client \"%s\" is %d.\n", client.getInetAddress().toString(), number);
                    outputStream.write("Welcome! Guess the number!\n".getBytes());

                    // Game
                    for (int tries = 0; tries < 6; tries++) {
                        try {
                            int guess = readNumber(inputStream, 10);
                            System.out.printf("Client %s guessed %d\n", client.getInetAddress().toString(), guess);
                            if (guess < number) {
                                outputStream.write("You guessed too small.\n".getBytes());
                            } else if (guess > number) {
                                outputStream.write("You guessed too big.\n".getBytes());
                            } else {
                                // Game won
                                System.out.printf("Client \"%s\" has won the game.", client.getInetAddress().toString());
                                outputStream.write(("Hooray! You guessed it. The number was " + number + ".\n").getBytes());
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input.");
                            outputStream.write("Invalid input.\n".getBytes());
                            tries--;
                        }
                        if (tries == 5) {
                            // Game lost
                            System.out.printf("Client \"%s\" reached max number of tries.", client.getInetAddress().toString());
                            outputStream.write(("You have lost. the number was " + number + ".\n").getBytes());
                        }
                    }

                    // Game finished
                    outputStream.write("Game finished.".getBytes());
                    outputStream.close();
                    inputStream.close();
                    client.close();
                } catch (SocketException ignored) {
                    System.out.printf("Client %s disconnected.\n", client.getInetAddress().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a number from an {@link InputStream}.
     * Reads until '\n' or until max number of digits is read
     */
    private static int readNumber(InputStream inputStream, int maxDigits) throws IOException, NumberFormatException {
        byte[] buffer = new byte[maxDigits];
        for (int i = 0; i < maxDigits; i++) {
            buffer[i] = (byte) inputStream.read();
            if (buffer[i] == '\n') {
                break;
            }
        }
        return Integer.parseInt(new String(buffer).trim());
    }
}
