package de.uulm.in.vs.grn.chat.client;

import de.uulm.in.vs.grn.chat.client.command.UnexpectedResponseException;
import de.uulm.in.vs.grn.chat.client.command.request.*;
import de.uulm.in.vs.grn.chat.client.command.response.*;
import de.uulm.in.vs.grn.chat.client.connection.CommandConnection;
import de.uulm.in.vs.grn.chat.client.connection.InvalidUsernameException;
import de.uulm.in.vs.grn.chat.client.connection.PubSubConnection;
import de.uulm.in.vs.grn.chat.client.pubsub.ChatMessage;
import de.uulm.in.vs.grn.chat.client.pubsub.Event;
import de.uulm.in.vs.grn.chat.client.pubsub.EventListener;
import de.uulm.in.vs.grn.chat.client.pubsub.MessageListener;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GRNCPClient implements MessageListener, EventListener, Runnable {
    private CommandConnection commandConnection;
    private PubSubConnection pubSubConnection;

    private ScheduledExecutorService pingTicker = Executors.newScheduledThreadPool(1);

    private List<MessageListener> messageListeners = new LinkedList<>();
    private List<EventListener> eventListeners = new LinkedList<>();

    /**
     * Connects to GRNCP server
     */
    public GRNCPClient(String host, int commandPort, int pubSubPort) throws IOException {
        commandConnection = new CommandConnection(host, commandPort);
        pubSubConnection = new PubSubConnection(host, pubSubPort);

        pubSubConnection.addOnEventListener(this);
        pubSubConnection.addOnMessageListener(this);

        Debug.println("[GRNCPClient] Connection established.");
    }

    /**
     * Tries to log in
     *
     * @param username Username to use
     * @return true if login was successful, false otherwise
     * @throws IOException
     * @throws InvalidUsernameException if username does not conform to specification.
     */
    public boolean login(String username) throws IOException, InvalidUsernameException, UnexpectedResponseException {
        if (username.length() < 3) {
            throw new InvalidUsernameException("Username too short");
        }
        if (username.length() > 15) {
            throw new InvalidUsernameException("Username too long");
        }
        if (!Util.isBasicASCII(username)) {
            throw new InvalidUsernameException("Username contains invalid characters.");
        }

        Debug.println("Logging in");
        Response loginResponse;
        try {
            loginResponse = commandConnection.sendCommand(new LoginRequest(username));
        } catch (TimeoutException e) {
            e.printStackTrace();
            return false;
        }
        Debug.println("LOGIN RESPONSE: " + loginResponse);
        if (loginResponse instanceof LoggedinResponse) {
            return true;
        } else if (loginResponse instanceof ErrorResponse) {
            System.err.println("Error logging in: " + loginResponse);
            return false;
        } else {
            throw new UnexpectedResponseException("Unexpected response to login request: " + loginResponse);
        }
    }

    public void addMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    public void addEventListener(EventListener eventListener) {
        eventListeners.add(eventListener);
    }

    @Override
    public void onChatMessage(ChatMessage chatMessage) {
        messageListeners.forEach(messageListener -> messageListener.onChatMessage(chatMessage));
    }

    @Override
    public void onEvent(Event event) {
        eventListeners.forEach(eventListener -> eventListener.onEvent(event));
    }

    public SentResponse sendChatMessage(String message) throws InvalidChatMessageException, IOException, TimeoutException, UnexpectedResponseException {
        SendRequest r = new SendRequest(message);
        Response res = commandConnection.sendCommand(r);
        if (res instanceof SentResponse) {
            return (SentResponse) res;
        } else {
            System.err.println(res);
            throw new UnexpectedResponseException();
        }
    }

    /**
     * Start listening for server messages and send periodic pings
     */
    @Override
    public void run() {
        Debug.println("Client starting connections");
        new Thread(pubSubConnection).start();
        new Thread(commandConnection).start();
        pingTicker.scheduleAtFixedRate(() -> {
            try {
                ping();
            } catch (UnexpectedResponseException | IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }, 9, 9, TimeUnit.MINUTES);
    }

    /**
     * Send ping to server
     *
     * @throws UnexpectedResponseException if response is not of type {@link PongResponse}
     */
    public PongResponse ping() throws UnexpectedResponseException, IOException, TimeoutException {
        Response r = commandConnection.sendCommand(new PingRequest());
        if (r instanceof PongResponse) return (PongResponse) r;
        throw new UnexpectedResponseException();
    }

    /**
     * Logs the user out
     *
     * @return true if user has been logged out.
     */
    public boolean logout() throws IOException, TimeoutException {
        Response r = commandConnection.sendCommand(new ByeRequest());
        return r instanceof ByeByeResponse || r instanceof ExpiredResponse;
    }
}
