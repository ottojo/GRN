package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.Debug;
import de.uulm.in.vs.grn.chat.client.command.request.Request;
import de.uulm.in.vs.grn.chat.client.command.response.Response;
import de.uulm.in.vs.grn.chat.client.command.response.UnknownResponseException;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Connection for sending {@link Request}s and receiving {@link Response}s.
 */
public class CommandConnection extends Connection {
    private Response lastResponse;

    private CyclicBarrier responseBarrier = new CyclicBarrier(2);

    public CommandConnection(String host, int port) throws IOException {
        super(host, port);
    }

    /**
     * Sends a message and returns the response when it's received.
     *
     * @param message
     * @return
     * @throws IOException
     * @throws TimeoutException if no response is received within 10 seconds.
     */
    public Response sendCommand(Request message) throws IOException, TimeoutException {
        sendMessage(message);
        Debug.println("[CommandConnection] sent command, waiting for response");
        try {
            responseBarrier.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        Debug.println("[CommandConnection] got response");
        return lastResponse;
    }

    @Override
    protected void onMessageFromServer(Message message) {
        try {
            lastResponse = Response.parse(message);

            if (responseBarrier.getNumberWaiting() == 0) {
                System.err.println("[CommandConnection] Received message from server but no one is waiting for a" +
                        " response: " + message);
            }

            responseBarrier.await();
        } catch (InterruptedException | UnknownResponseException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
