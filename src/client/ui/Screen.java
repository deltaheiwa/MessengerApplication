package client.ui;

import javax.swing.*;

public enum Screen {
    USERNAME_PROMPT(new UsernamePrompt()),
    CHAT(new Chat());

    private final JPanel panel;

    Screen(JPanel panel) {
        this.panel = panel;
    }

    public JPanel getPanel() {
        return panel;
    }
}
