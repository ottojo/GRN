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

                Socket clientSocket = guessingServer.accept();
                System.out.printf("Client %s:%d connected.\n",
                        clientSocket.getInetAddress().toString(), clientSocket.getPort());

                try {
                    InputStream inputStream = clientSocket.getInputStream();
                    OutputStream outputStream = clientSocket.getOutputStream();

                    // Game init
                    int number = ThreadLocalRandom.current().nextInt(50);
                    System.out.printf("Number for client is %d.\n", number);
                    outputStream.write(("Welcome!\n").getBytes());
                    outputStream.write("Guess the number!\n".getBytes());

                    // Game
                    for (int tries = 0; tries < 6; tries++) {
                        try {
                            int guess = readNumber(inputStream, 10);
                            System.out.printf("guessed %d\n", guess);
                            if (guess < number) {
                                outputStream.write("You guessed too small.\n".getBytes());
                            } else if (guess > number) {
                                outputStream.write("You guessed too big.\n".getBytes());
                            } else {
                                // Game won
                                System.out.print("Client has won the game.\n");
                                outputStream.write(("Hooray! You guessed it. The number was " + number + ".\n").getBytes());
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.print("Invalid input.\n");
                            outputStream.write("Invalid input.\n".getBytes());
                            // Do not count this try
                            tries--;
                        }
                        if (tries == 5) {
                            // Game lost
                            System.out.print("Client reached max number of tries.\n");
                            outputStream.write(("You have lost. the number was " + number + ".\n").getBytes());
                        }
                    }

                    // Game finished
                    outputStream.write("Game finished.".getBytes());
                    outputStream.close();
                    inputStream.close();
                    clientSocket.close();
                } catch (SocketException ignored) {
                    System.out.print("Client disconnected.\n");
                } catch (IOException e) {
                    e.printStackTrace();
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
