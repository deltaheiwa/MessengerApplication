package server.network;

import common.APISkeleton;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server implements APISkeleton {
    private int port;
    private String serverName;
    private String[] bannedPhrases;

    private final List<ConnectedClient> connectedClients;

    private final ServerSocket serverSocket;

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
                while (!socket.isClosed() && (line = in.readLine()) != null) {
                    String[] broken_up_endpoint = line.split(" ", 2);
                    String endpoint = broken_up_endpoint[0];
                    switch (endpoint) {
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
                            StringBuilder connectedClientsValue = new StringBuilder();
                            connectedClientsValue.append("CONNECTED_CLIENTS ");
                            this.connectedClients.forEach(client -> connectedClientsValue.append(client.toString()).append(" "));
                            out.println(connectedClientsValue);
                            out.flush();
                            break;
                        case "GET_BANNED_PHRASES":
                            out.println("BANNED_PHRASES");
                            Arrays.stream(bannedPhrases).forEach(out::println);
                            out.println();
                            out.flush();
                            break;
                        case "DISCONNECT":
                            socket.close();
                            break;
                        case "POST_MESSAGE":
                            Message message = parseMessage(broken_up_endpoint[1]);
                            if (message.isBanned()) {
                                out.println("MESSAGE_REJECTED " + message.getBannedPhrase());
                                out.flush();
                                break;
                            }
                            if (message.getRecipients().length == 0) {
                                notifyClients("MESSAGE " + message.getSender() + " " + message.getContent());
                            } else if (message.areExcluded()) {
                                connectedClients.stream().filter(client -> !Arrays.asList(message.getRecipients()).contains(client.toString())).forEach(client -> {
                                    try {
                                        client.sendMessage("PRIVATE_MESSAGE " + message.getSender() + " " + message.getContent());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                connectedClients.stream().filter(client -> Arrays.asList(message.getRecipients()).contains(client.toString())).forEach(client -> {
                                    try {
                                        client.sendMessage("PRIVATE_MESSAGE " + message.getSender() + " " + message.getContent());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
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

    private boolean isRealClient(String clientName) {
        return connectedClients.stream().anyMatch(client -> client.toString().equals(clientName));
    }

    private Message parseMessage(String message) {
        String[] broken_up_message = message.split(" ", 2);
        String sender = broken_up_message[0];
        Queue<String> content = new ConcurrentLinkedQueue<>(List.of(broken_up_message[1].split(" ")));
        List<String> recipientList = new ArrayList<>();
        boolean isExcluded = false;
        boolean isBanned = false;
        String bannedPhrase = null;

        for (String word : content) {
            if (word.startsWith("!@")) {
                String clientName = word.substring(2);
                if (isRealClient(clientName) && !clientName.equals(sender)) {
                    if (!isExcluded) {
                        recipientList.clear();
                    }
                    recipientList.add(clientName);
                    isExcluded = true;
                    content.remove(word);
                } else {
                    break;
                }
            } else if (word.startsWith("@")) {
                String clientName = word.substring(1);
                if (isRealClient(clientName) && !clientName.equals(sender)) {
                    recipientList.add(clientName);
                    content.remove(word);
                } else {
                    break;
                }
            }
        }

        String collectedContent = content.stream().reduce((s1, s2) -> s1 + " " + s2).orElse("").trim();

        for (String phrase : bannedPhrases) {
            if (
                    collectedContent.contains(" " + phrase + " ") ||
                    collectedContent.startsWith(phrase + " ") ||
                    collectedContent.endsWith(" " + phrase) ||
                    collectedContent.equals(phrase)
            ) {
                isBanned = true;
                bannedPhrase = phrase;
                break;
            }
        }

        return new Message(
                collectedContent,
                sender,
                recipientList.toArray(new String[0]),
                isExcluded,
                isBanned,
                bannedPhrase
        );

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
    public void sendMessage(String m, String sender) throws Exception {}

    @Override
    public String[] getBannedPhrases() {
        return bannedPhrases;
    }

    @Override
    public void getConnectedClients() { }

    @Override
    public void disconnect() {
        connectedClients.forEach(client -> {
            try {
                client.sendMessage("DISCONNECT");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
