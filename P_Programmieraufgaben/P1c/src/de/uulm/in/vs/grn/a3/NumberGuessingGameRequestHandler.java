package de.uulm.in.vs.grn.a3;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class NumberGuessingGameRequestHandler implements Runnable {
    private static final int MAX_TRIES = 6;

    private Socket client;

    public NumberGuessingGameRequestHandler(Socket soc) {
        this.client = soc;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(client.getOutputStream()),true);
            //create a random number between 0 and 50
            int number = ThreadLocalRandom.current().nextInt(50);
            System.out.println("RandomNumber selected: " + number + "\n");
            pw.println("Random Number selected. Enter your guess: ");

            for (int i = 0; i < MAX_TRIES; i++) {
                try {
                    int inputNumber = getNumberFromClient(br);
                    System.out.println("Guess #" + i + 1 + " is " + inputNumber + "\n");
                    if (number < inputNumber) {
                        pw.println("Your guess was bigger than the random number. ");
                    }
                    else if (number > inputNumber) {
                        pw.println("Your guess was smaller than the random number ");
                    }
                    else {
                        pw.println("You guessed correct.");
                        break;
                    }
                    pw.println(("You have " + (MAX_TRIES - i - 1) + " remaining try/tries."));

                    if (i == 5) {
                        pw.println("No tries remaining. YOU LOST");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("User input was invalid. ");
                    pw.println("User input was invalid. ");
                    i--; //invalid input does not effect the game
                }
            }

            pw.close();
            br.close();
            client.close();
        } catch (IOException ignored) {
            //not handling IOException
        }


    }


    private static int getNumberFromClient(BufferedReader br) throws IOException {
        String str_input = br.readLine();
        return Integer.parseInt(new String(str_input).trim());
    }
}
