package de.uulm.in.vs.grn.a3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


    public class NumberGuessingGameThreadedServer {

        private static final int PORT = 5555;
        private static final int MAX_THREADS  = 4;
        private static  ServerSocket server = null;
        private static Executor executor = Executors.newFixedThreadPool(MAX_THREADS);

        public static void main(String[]args) throws IOException {
            try{
                server = new ServerSocket(PORT);
                System.out.println("Server started.");
            }catch (IOException e){
                e.printStackTrace();
            }

            for(int i = 0; i < 4; i++){
                while(!server.isClosed()) {
                    Socket soc = server.accept();
                    System.out.println("Client connected to Server");
                    Runnable workerThread = new NumberGuessingGameRequestHandler(soc);
                    executor.execute(workerThread);
                }
            }
            //threadFactory();
        }
        /*
        private static void threadFactory() throws IOException {
            while(!server.isClosed()){
                Socket soc = server.accept();
                System.out.println("Client connected to Server");
                new Thread(new NumberGuessingGameRequestHandler(soc)).start();
            }

        }
       */
    }

