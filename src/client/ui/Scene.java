package client.ui;

import client.network.Message;

import javax.swing.*;
import java.util.List;

public abstract class Scene extends JPanel {
    public abstract void updateMessages(List<Message> messages);

    public abstract void updateUsers(String[] users);
}
