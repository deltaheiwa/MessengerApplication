package client;

import client.network.Client;
import client.ui.WindowWrapper;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(WindowWrapper::new);
        Thread networkThread = new Thread(() -> {
            try {
                Client client = new Client();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}