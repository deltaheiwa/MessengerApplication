package client.ui;

import client.network.Message;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Chat extends Scene {
    private final JTextArea chatArea;
    private final JPanel sidebarPanel;

    public Chat() {
        super();
        setLayout(new BorderLayout());

        JTextField inputField = getTextField();
        add(inputField, BorderLayout.SOUTH);

        chatArea = new JTextArea();
        chatArea.setBackground(Color.white);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        add(chatScrollPane, BorderLayout.CENTER);

        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));

        JScrollPane sidebarScrollPane = new JScrollPane(sidebarPanel);
        sidebarScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(sidebarScrollPane, BorderLayout.EAST);
    }

    private JTextField getTextField() {
        JTextField inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(0, 30));
        inputField.addActionListener(_ -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                inputField.setText("");
                if (message.equals("/help")) {
                    WindowWrapper.getInstance().getClient().getBannedPhrases();
                    return;
                }
                WindowWrapper.getInstance().getClient().sendMessage(message, WindowWrapper.getInstance().getClient().getUsername());
            }
        });
        return inputField;
    }

    @Override
    public void updateMessages(List<Message> messages) {
        StringBuilder sb = new StringBuilder();
        sb.append("Welcome to the chat!\nUse command \"/help\" to get a list of banned phrases\n")
                .append("To message a specific user, type \"@username message\"\n")
                .append("Or to exclude a specific user from your recipients, type \"!@username message\"\n");
        for (Message message : messages) {
            if (message.getSender().equals("SERVER")) {
                sb.append("\n\n").append(message).append("\n");
                continue;
            }
            sb.append("\n").append(message);
        }
        chatArea.setText(sb.toString());
        chatArea.revalidate();
        chatArea.repaint();
    }

    @Override
    public void updateUsers(String[] users) {
        sidebarPanel.removeAll();
        for (String user : users) {
            JLabel userLabel = new JLabel(user);
            sidebarPanel.add(userLabel);
        }
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }
}
