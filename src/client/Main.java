package client;

import client.ui.WindowWrapper;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(WindowWrapper::getInstance);
    }
}