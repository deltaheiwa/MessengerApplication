package client.ui;

import javax.swing.*;
import java.awt.*;

public class WindowWrapper extends JFrame {
    private client.ui.Screen currentScreen;
    private final JLayeredPane mainPanel;

    public WindowWrapper() {
        super("Window Wrapper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setMinimumSize(getSize());
        setLocationRelativeTo(null);

        mainPanel = new JLayeredPane();
        mainPanel.setPreferredSize(new Dimension(getWidth(), getHeight()));

        setCurrentScreen(client.ui.Screen.USERNAME_PROMPT);

        add(mainPanel);
        setVisible(true);
    }

    public void setCurrentScreen(client.ui.Screen screen) {
        if (currentScreen != null) {
            mainPanel.remove(currentScreen.getPanel());
        }
        currentScreen = screen;
        currentScreen.getPanel().setBounds(0, 0, getWidth(), getHeight());
        mainPanel.add(currentScreen.getPanel(), JLayeredPane.DEFAULT_LAYER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
