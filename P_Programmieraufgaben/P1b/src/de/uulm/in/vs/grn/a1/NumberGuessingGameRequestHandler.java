package de.uulm.in.vs.grn.a1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ThreadLocalRandom;

public class NumberGuessingGameRequestHandler implements Runnable {
    private Socket clientSocket;
    private static int gameNumber = 0;  // Just for debug purposes, to correlate console output with socket

    NumberGuessingGameRequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        gameNumber++;
        System.out.printf("[%d] Client %s:%d connected.\n",
                gameNumber, clientSocket.getInetAddress().toString(), clientSocket.getPort());

        try {
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            // Game init
            int number = ThreadLocalRandom.current().nextInt(50);
            System.out.printf("[%d] Number for client is %d.\n", gameNumber, number);
            outputStream.write(("Welcome! You are game Nr. " + gameNumber + "\n").getBytes());
            outputStream.write("Guess the number!\n".getBytes());

            // Game
            for (int tries = 0; tries < 6; tries++) {
                try {
                    int guess = readNumber(inputStream, 10);
                    System.out.printf("[%d] guessed %d\n", gameNumber, guess);
                    if (guess < number) {
                        outputStream.write("You guessed too small.\n".getBytes());
                    } else if (guess > number) {
                        outputStream.write("You guessed too big.\n".getBytes());
                    } else {
                        // Game won
                        System.out.printf("[%d] Client has won the game.\n", gameNumber);
                        outputStream.write(("Hooray! You guessed it. The number was " + number + ".\n").getBytes());
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.printf("[%d] Invalid input.\n", gameNumber);
                    outputStream.write("Invalid input.\n".getBytes());
                    // Do not count this try
                    tries--;
                }
                if (tries == 5) {
                    // Game lost
                    System.out.printf("[%d] Client reached max number of tries.\n", gameNumber);
                    outputStream.write(("You have lost. the number was " + number + ".\n").getBytes());
                }
            }

            // Game finished
            outputStream.write("Game finished.".getBytes());
            outputStream.close();
            inputStream.close();
            clientSocket.close();
        } catch (SocketException ignored) {
            System.out.printf("[%d] Client disconnected.\n", gameNumber);
        } catch (IOException e) {
            try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
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
            } else if (buffer[i] == -1) {
                throw new IOException("EOF");
            }
        }
        return Integer.parseInt(new String(buffer).trim());
    }
}
