package de.uulm.in.vs.grn.chat.client.command.request;

public class LoginRequest extends Request {
    public LoginRequest(String username) {
        super("LOGIN");
        fields.put("Username", username);
    }
}
