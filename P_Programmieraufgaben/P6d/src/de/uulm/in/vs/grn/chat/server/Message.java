package de.uulm.in.vs.grn.chat.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Message {
    private static final String PROTOCOL_VERSION = "GRNCP/0.1";
    protected String command;
    protected Map<String, String> fields = new HashMap<>();

    public Message(String command) {
        this.command = command;
    }

    protected Message() {
    }

    /**
     * Parse Message from client
     *
     * @param contents Message without trailing free line
     * @return Parsed message containing command and fields
     * @throws UnknownProtocolException if protocol string does not match {@value #PROTOCOL_VERSION}
     */
    public static Message parse(String contents) throws UnknownProtocolException {
        Debug.println("Parsing message:");
        Debug.println(contents);
        Message m = new Message();
        String[] lines = contents.split("\r\n");
        String[] statusLine = lines[0].split(" ");
        if (!Objects.equals(statusLine[1], PROTOCOL_VERSION)) {
            throw new UnknownProtocolException(statusLine[1]);
        }
        m.command = statusLine[0];
        String lastKey = "";
        for (int i = 1; i < lines.length; i++) {
            String l = lines[i];
            if (!Objects.equals(l, "")) {
                String[] f = l.split(": ");
                if (f.length >= 2) {
                    m.fields.put(f[0], f[1]);
                    lastKey = f[0];
                } else {
                    m.fields.put(lastKey, m.fields.get(lastKey) + f[0]);
                }
            }
        }
        return m;
    }

    /**
     * Assembles the message for sending to a client
     *
     * @return
     */
    public byte[] build() {
        StringBuilder result = new StringBuilder();
        result.append(PROTOCOL_VERSION).append(" ").append(command).append("\r\n");
        if (fields.containsKey("Date")) {
            result.append("Date").append(": ").append(fields.get("Date")).append("\r\n");
        }
        fields.forEach((key, val) -> {
            if (!key.equals("Date")) {
                result.append(key).append(": ").append(val).append("\r\n");
            }
        });
        result.append("\r\n");

        Debug.println("## Assembled message ##");
        Debug.println(result.toString());
        Debug.println("##                   ##");

        return result.toString().getBytes();
    }

    public String getCommand() {
        return command;
    }

    public Map<String, String> getAllFields() {
        return new HashMap<>(fields);
    }

    @Override
    public String toString() {
        return "Message{" +
                "command='" + command + '\'' +
                ", fields=" + fields +
                '}';
    }
}