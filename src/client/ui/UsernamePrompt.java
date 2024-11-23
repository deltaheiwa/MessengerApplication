package client.ui;

import client.network.Client;
import client.network.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class UsernamePrompt extends Scene {
    public UsernamePrompt() {
        super();
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 50));
        JPanel nestedUI = new JPanel();
        nestedUI.setLayout(new GridLayout(3, 1));
        add(nestedUI, BorderLayout.CENTER);

        JLabel label = new JLabel("Enter your username:");
        JTextField textField = new JTextField();
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textField.getText();
                if (!username.isEmpty()) {
                    try {
                        WindowWrapper windowWrapper = WindowWrapper.getInstance();
                        boolean accepted = windowWrapper.getClient().connect(username);
                        if (accepted) {
                            windowWrapper.setCurrentScreen(Screen.CHAT);
                        } else {
                            JOptionPane.showMessageDialog(null, "Username already taken");
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        nestedUI.add(label);
        nestedUI.add(textField);
        nestedUI.add(submitButton);
    }

    @Override
    public void updateMessages(List<Message> messages) {}
}
