package client.network;

import client.ui.WindowWrapper;
import common.APISkeleton;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client implements APISkeleton {
    private BufferedReader in;
    private PrintWriter out;

    private List<Message> messages;

    public Client() {
        this.messages = new ArrayList<>();
    }

    @Override
    public boolean connect(String username) throws IOException {
        Socket socket = new Socket("localhost", 8790);
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
            System.out.println(Arrays.toString(getServerName));
            messages.add(new Message("Connected to " + getServerName[1], "SERVER", MessageType.NORMAL));
            onConnect();
            return true;
        }
    }

    private void getConnectedClientsTimer() {
        new Thread(() -> {
            while (true) {
                try {
                    String[] connectedClients = getConnectedClients();
                    System.out.println(Arrays.toString(connectedClients));

                    Thread.sleep(5000);
                } catch (IOException | InterruptedException e) {
                    System.out.println("Could not get connected clients");
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
                    if (getMessageType[0].equals("MESSAGE")) {
                        String[] getSender = getMessageType[1].split(" ", 2);
                        messages.add(new Message(getSender[1], getSender[0], MessageType.NORMAL));
                    } else if (getMessageType[0].equals("PRIVATE_MESSAGE")) {
                        String[] getSender = getMessageType[1].split(" ", 2);
                        messages.add(new Message(getSender[1], getSender[0], MessageType.PRIVATE));
                    }
                    WindowWrapper.getInstance().updateMessages();
                } catch (IOException e) {
                    System.out.println("Could not get message");
                }
            }
        }).start();
    }

    public void onConnect() {
        System.out.println("Connected to the server");
        serverListener();
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public int sendMessage(String m) throws Exception {
        return 0;
    }

    @Override
    public int sendMessageToSpecificClient(String m, int clientID) throws Exception {
        return 0;
    }

    @Override
    public int sendMessageToMultipleClients(String m, int[] clientIDs) throws Exception {
        return 0;
    }

    @Override
    public int sendMessageExcept(String m, int[] clientIDs) throws Exception {
        return 0;
    }

    @Override
    public String[] getBannedPhrases() throws Exception {
        return new String[0];
    }

    @Override
    public String[] getConnectedClients() throws IOException {
        out.println("GET_CONNECTED_CLIENTS");
        out.flush();

        List<String> connectedClients = new ArrayList<>();
        String line;
        while ((line = in.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            connectedClients.add(line);
        }
        return connectedClients.toArray(new String[0]);
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
