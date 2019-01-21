package de.uulm.in.vs.grn.chat.client.command.request;

public class SendRequest extends Request {
    /**
     * @throws InvalidChatMessageException if size of chat message exceeds 512 bytes.
     */
    public SendRequest(String chatMessage) throws InvalidChatMessageException {
        super("SEND");
        if (chatMessage == null || chatMessage.equals("")) {
            throw new InvalidChatMessageException("Chat message must not be empty.");
        }
        if (chatMessage.getBytes().length > 512) {
            throw new InvalidChatMessageException("Maximum message length exceeded.");
        }
        fields.put("Text", chatMessage);
    }
}
