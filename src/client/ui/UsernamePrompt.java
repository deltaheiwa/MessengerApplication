package client.ui;

import javax.swing.*;

public class UsernamePrompt extends JPanel {
    public UsernamePrompt() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Enter your username:");
        JTextField textField = new JTextField();
        JButton submitButton = new JButton("Submit");

        add(label);
        add(textField);
        add(submitButton);
    }
}
