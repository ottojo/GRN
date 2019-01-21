package de.uulm.in.vs.grn.chat.client.command.response;

import de.uulm.in.vs.grn.chat.client.Debug;
import de.uulm.in.vs.grn.chat.client.connection.Message;

public class Response extends Message {


    public static Response parse(Message message) throws UnknownResponseException {
        Debug.println("Parsing Response: " + message);
        Response r;
        switch (message.getCommand()) {
            case LoggedinResponse.COMMAND:
                r = new LoggedinResponse();
                break;
            case SentResponse.COMMAND:
                r = new SentResponse();
                break;
            case PongResponse.COMMAND:
                r = new PongResponse();
                break;
            case ExpiredResponse.COMMAND:
                r = new ExpiredResponse();
                break;
            case ErrorResponse.COMMAND:
                r = new ErrorResponse();
                break;
            case ByeByeResponse.COMMAND:
                r = new ByeByeResponse();
                break;
            default:
                throw new UnknownResponseException(message.getCommand());
        }


        r.fields = message.getAllFields();

        return r;
    }

    public String getCommand() {
        return command;
    }
}
