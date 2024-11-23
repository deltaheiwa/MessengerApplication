package client.network;

import client.ui.WindowWrapper;
import common.APISkeleton;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Client implements APISkeleton {
    private String username;
    private BufferedReader in;
    private PrintWriter out;

    private final List<Message> messages;

    public Client() {
        this.messages = new ArrayList<>();
    }

    @Override
    public boolean connect(String username) throws IOException {
        Properties properties = new Properties();
        // Done purely for convenience
        int port;
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream("res/server.properties"))) {
            properties.load(reader);
            port = Integer.parseInt(properties.getProperty("SERVER_PORT"));
        } catch (IOException e) {
            System.out.println("Could not load properties file. Defaulting to 8790");
            port = 8790;
        }
        Socket socket = new Socket("localhost", port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream());

        out.println("CONNECT");
        out.println(username);
        out.flush();

        String response = in.readLine();
        if (response.equals("DENIED")) {
            socket.close();
            return false;
        } else {
            String[] getServerName = response.split(" ", 2);
            messages.add(new Message("Connected to " + getServerName[1], "SERVER", MessageType.NORMAL));
            this.username = username;
            onConnect();
            return true;
        }
    }

    @Override
    public void sendMessage(String m, String sender)  {
        out.println("POST_MESSAGE " + sender + " " + m);
        out.flush();
    }

    private void getConnectedClientsTimer() {
        new Thread(() -> {
            while (true) {
                try {
                    getConnectedClients();
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println("Could not sleep. Mood.");
                }
            }
        }).start();
    }

    private void serverListener() {
        new Thread(() -> {
            while (true) {
                try {
                    String message = in.readLine();
                    if (message == null) {
                        System.out.println("Server disconnected");
                        break;
                    }
                    String[] getMessageType = message.split(" ", 2);
                    switch (getMessageType[0]) {
                        case "MESSAGE", "PRIVATE_MESSAGE" -> {
                            String[] getSender = getMessageType[1].split(" ", 2);
                            messages.add(new Message(getSender[1], getSender[0], getMessageType[0].equals("MESSAGE") ? MessageType.NORMAL : MessageType.PRIVATE));
                            WindowWrapper.getInstance().updateMessages();
                        }
                        case "CONNECTED_CLIENTS" -> {
                            String[] clients = getMessageType[1].split(" ");
                            WindowWrapper.getInstance().updateUsers(clients);
                        }
                        case "MESSAGE_REJECTED" -> WindowWrapper.getInstance().bannedWordPopup(getMessageType[1]);
                        case "BANNED_PHRASES" -> {
                            StringBuilder helpMessage = new StringBuilder("You are not allowed to use any of the following words or phrases: ");
                            String phrase;
                            while ((phrase = in.readLine()) != null) {
                                if (phrase.isEmpty()) {
                                    break;
                                }
                                helpMessage.append(phrase).append(", ");
                            }
                            helpMessage.delete(helpMessage.length() - 2, helpMessage.length());
                            messages.add(new Message(helpMessage.toString(), "SERVER", MessageType.NORMAL));
                            WindowWrapper.getInstance().updateMessages();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Could not get content");
                }
            }
        }).start();
    }

    public void onConnect() {
        System.out.println("Connected to the server as " + username);
        serverListener();
        getConnectedClientsTimer();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String[] getBannedPhrases() {
        out.println("GET_BANNED_PHRASES");
        out.flush();
        return new String[0];
    }

    @Override
    public void getConnectedClients() {
        out.println("GET_CONNECTED_CLIENTS");
        out.flush();
    }

    @Override
    public void disconnect() {
        if (out == null) {
            return;
        }
        out.println("DISCONNECT");
        out.flush();
    }
}
