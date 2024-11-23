package server.network;

import common.APISkeleton;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Server implements APISkeleton {
    private int port;
    private String serverName;
    private String[] bannedPhrases;

    private List<ConnectedClient> connectedClients;

    private ServerSocket serverSocket;

    public Server() throws IOException {
        loadProperties("res/server.properties");
        serverSocket = new ServerSocket(port);
        connectedClients = new ArrayList<>();
    }

    public Server(String pathToConfig) throws IOException {
        loadProperties(pathToConfig);
        serverSocket = new ServerSocket(port);
        connectedClients = new ArrayList<>();
    }

    private void notifyClients(String message) {
        connectedClients.forEach(client -> {
            try {
                client.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void delegateThread(Socket socket) {
        new Thread(() -> {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                String clientID = "";

                String line;
                while ((line = in.readLine()) != null) {
                    switch (line) {
                        case "CONNECT":
                            String clientName = in.readLine();
                            boolean allowed = connect(clientName);
                            if (!allowed) {
                                out.println("DENIED");
                                out.flush();
                                socket.close();
                                break;
                            }
                            System.out.println("Connected: " + clientName);
                            notifyClients("MESSAGE SERVER " + clientName + " has connected");
                            connectedClients.add(new ConnectedClient(socket.getInetAddress().toString(), socket.getPort(), clientName, in, out));
                            clientID = clientName;
                            out.println("CONNECTED " + serverName);
                            out.flush();
                            break;
                        case "GET_CONNECTED_CLIENTS":
                            connectedClients.stream().map(ConnectedClient::toString).forEach(out::println);
                            out.println();
                            out.flush();
                            break;
                        case "GET_BANNED_PHRASES":
                            Arrays.stream(bannedPhrases).forEach(out::println);
                            out.println();
                            out.flush();
                            break;
                        case "DISCONNECT":
                            socket.close();
                            break;
                        default:
                            System.out.println("Unknown endpoint: " + line);
                            break;
                    }
                }


                String finalClientID = clientID;
                connectedClients.removeIf(client -> client.toString().equals(finalClientID));
                System.out.println("Disconnected: " + clientID);
                notifyClients("MESSAGE SERVER " + clientID + " has disconnected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadProperties(String configPath) {
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configPath))) {
            properties.load(reader);

            port = Integer.parseInt(properties.getProperty("SERVER_PORT"));
            serverName = properties.getProperty("SERVER_NAME");
            bannedPhrases = Arrays.stream(properties.getProperty("BANNED_PHRASES").split(",")).map(String::trim).toArray(String[]::new);
            System.out.println("Loaded properties");
        } catch (IOException e) {
            System.out.println("Could not find server.properties file");
            System.out.println("Creating a template file");
            port = 8790;
            properties.setProperty("SERVER_PORT", port + "");
            serverName = "Template";
            properties.setProperty("SERVER_NAME", serverName);
            bannedPhrases = new String[]{"nword", "javascript", "elon musk"};
            properties.setProperty("BANNED_PHRASES", String.join(",", bannedPhrases));
            try {
                properties.store(new OutputStreamWriter(new FileOutputStream(configPath)), null);
            } catch (IOException e1) {
                throw new RuntimeException("Could not create template file");
            }
        }
    }

    public void listen() {
        System.out.println("Listening on port " + port + " as " + serverName);
        while (true) {
            try {
                delegateThread(serverSocket.accept());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean connect(String username) {
        for (ConnectedClient client : connectedClients) {
            if (client.toString().equals(username)) {
                return false;
            }
        }
        return true;
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
    public String[] getConnectedClients() throws Exception {
        return connectedClients.stream().map(ConnectedClient::toString).toArray(String[]::new);
    }

    @Override
    public void disconnect() {

    }
}
