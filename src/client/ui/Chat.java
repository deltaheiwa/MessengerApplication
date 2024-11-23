package client.ui;

import client.network.Message;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Chat extends Scene {
    private final JTextArea chatArea;

    public Chat() {
        super();
        setLayout(new BorderLayout());

        JTextField inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(0, 30));
        add(inputField, BorderLayout.SOUTH);

        chatArea = new JTextArea();
        chatArea.setBackground(Color.white);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        add(chatScrollPane, BorderLayout.CENTER);

        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));

        JScrollPane sidebarScrollPane = new JScrollPane(sidebarPanel);
        sidebarScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(sidebarScrollPane, BorderLayout.EAST);
    }

    @Override
    public void updateMessages(List<Message> messages) {
        StringBuilder sb = new StringBuilder();
        for (Message message : messages) {
            sb.append("\n\n").append(message);
        }
        chatArea.setText(sb.toString());
        repaint();
    }
}
