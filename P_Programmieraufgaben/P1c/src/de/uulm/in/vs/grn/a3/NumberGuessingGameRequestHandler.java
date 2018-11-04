package de.uulm.in.vs.grn.a3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ThreadLocalRandom;

public class NumberGuessingGameRequestHandler implements Runnable {
    private static final int MAX_TRIES = 6;
    private Socket client;
    private int gameNumber;  // Just for debug purposes, to correlate console output with socket
    private static int gameCounter = 0;

    public NumberGuessingGameRequestHandler(Socket soc) {
        this.client = soc;
        gameNumber = gameCounter;
        gameCounter++;
    }

    @Override
    public void run() {
        System.out.printf("[%d] Client %s:%d connected.\n",
                gameNumber, client.getInetAddress().toString(), client.getPort());

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter pw = new PrintWriter(client.getOutputStream(), true);

            // Game init
            int number = ThreadLocalRandom.current().nextInt(50);
            System.out.printf("[%d] Number for client is %d.\n", gameNumber, number);

            pw.println(("Welcome! You are game Nr. " + gameNumber));
            pw.write("Guess the number!\n");

            // Game
            for (int i = 0; i < MAX_TRIES; i++) {
                try {
                    int inputNumber = getNumberFromClient(br);
                    System.out.printf("[%d] guessed %d\n", gameNumber, inputNumber);
                    if (inputNumber < number) {
                        pw.println("You guessed too small.");
                    } else if (inputNumber > number) {
                        pw.println("You guessed too big.");
                    } else {
                        // Game won
                        System.out.printf("[%d] Client has won the game.\n", gameNumber);
                        pw.println(("Hooray! You guessed it. The number was " + number + "."));
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.printf("[%d] Invalid input.\n", gameNumber);
                    pw.println("Invalid input.");
                    // Do not count this try
                    i--;
                }
                if (i == 5) {
                    // Game lost
                    System.out.printf("[%d] Client reached max number of tries.\n", gameNumber);
                    pw.println("You have lost. the number was " + number + ".");
                }
            }

            // Game finished
            pw.println("Game finished.");
            pw.close();
            br.close();
            client.close();
        } catch (SocketException ignored) {
            System.out.printf("[%d] Client disconnected.\n", gameNumber);
        } catch (IOException e) {
            try {
                client.close();
            } catch (IOException ignored) {
            }
            e.printStackTrace();
        }
    }

    private static int getNumberFromClient(BufferedReader br) throws IOException {
        String str_input = br.readLine();
        if (str_input == null) {
            throw new IOException("EOF");
        }
        return Integer.parseInt(str_input.trim());
    }

}
