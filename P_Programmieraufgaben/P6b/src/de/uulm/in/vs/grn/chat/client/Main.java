package de.uulm.in.vs.grn.chat.client;

import de.uulm.in.vs.grn.chat.client.command.UnexpectedResponseException;
import de.uulm.in.vs.grn.chat.client.command.request.InvalidChatMessageException;
import de.uulm.in.vs.grn.chat.client.connection.InvalidUsernameException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class Main {

    private static GRNCPClient client;

    public static void main(String[] args) throws IOException {

        // GUI
        JFrame frame = new JFrame("GRNCP");
        frame.getContentPane().setLayout(new BorderLayout());

        JTextArea chatBox = new JTextArea(25, 120);
        chatBox.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatBox);

        JTextField inputBox = new JTextField("");
        inputBox.setMargin(new Insets(5, 5, 5, 5));

        JButton sendButton = new JButton("Send");

        JPanel sendPanel = new JPanel(new BorderLayout());
        sendPanel.add(sendButton, BorderLayout.LINE_END);
        sendPanel.add(inputBox, BorderLayout.CENTER);

        frame.getContentPane().add(chatScroll, BorderLayout.CENTER);
        frame.getContentPane().add(sendPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    client.logout();
                } catch (IOException | TimeoutException e1) {
                    e1.printStackTrace();
                }
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        // Chat
        Debug.enable();
        client = new GRNCPClient("grn-services.lxd-vs.uni-ulm.de", 8122, 8123);
        new Thread(client).start();

        // Append Chat messages to TextArea
        client.addMessageListener(chatMessage -> {
            chatBox.append(chatMessage.getDate() + " " + chatMessage.getUser() + ": " + chatMessage.getMessage() + "\n");
            JScrollBar vertical = chatScroll.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });

        // Append Server Events to TextArea
        client.addEventListener(event -> {
            chatBox.append(event.getDescription() + "\n");
            JScrollBar vertical = chatScroll.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });

        // Send Button
        sendButton.addActionListener(e -> {
            try {
                String input = inputBox.getText();
                // Chat commands (local)
                if (input.startsWith("//")) {
                    switch (input) {
                        case "//users":
                            chatBox.append(Arrays.deepToString(client.ping().getUsernames()) + "\n");
                    }
                } else {
                    client.sendChatMessage(input);
                }
                inputBox.setText("");
            } catch (InvalidChatMessageException | IOException | TimeoutException | UnexpectedResponseException e1) {
                e1.printStackTrace();
            }

        });

        // Enter Key <=> Send Button
        inputBox.addActionListener(e -> sendButton.doClick());

        boolean loggedIn = false;
        while (!loggedIn) {
            try {

                loggedIn = client.login(JOptionPane.showInputDialog("Username"));
            } catch (InvalidUsernameException e) {
                e.printStackTrace();
            } catch (UnexpectedResponseException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
