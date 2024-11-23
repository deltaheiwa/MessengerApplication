package client.ui;

import client.network.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowWrapper extends JFrame {
    private static WindowWrapper instance = null;

    private Screen currentScreen;
    private JLayeredPane mainPanel;
    private Client client;

    public WindowWrapper() {
        super("Window Wrapper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client != null) {
                    client.disconnect();
                }
            }
        });
        setSize(900, 500);
        setResizable(false);
        setLocationRelativeTo(null);

        mainPanel = new JLayeredPane();
        mainPanel.setPreferredSize(new Dimension(getWidth(), getHeight()));

        client = new Client();
        setCurrentScreen(Screen.USERNAME_PROMPT);

        add(mainPanel);
        setVisible(true);
    }

    public static WindowWrapper getInstance() {
        synchronized (WindowWrapper.class) {
            if (instance == null) {
                instance = new WindowWrapper();
            }
        }
        return instance;
    }

    public Client getClient() {
        return client;
    }

    public void updateMessages() {
        if (currentScreen == Screen.CHAT) {
            currentScreen.getPanel().updateMessages(client.getMessages());
        }
    }

    public void setCurrentScreen(Screen screen) {
        if (currentScreen != null) {
            mainPanel.remove(currentScreen.getPanel());
        }
        currentScreen = screen;
        currentScreen.getPanel().setBounds(0, 0, getWidth(), getHeight()-30);
        mainPanel.add(currentScreen.getPanel(), JLayeredPane.DEFAULT_LAYER);
        currentScreen.getPanel().updateMessages(client.getMessages());
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
