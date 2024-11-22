package server.network;

import java.io.*;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Properties;

public class Server {
    private int port;
    private String serverName;
    private String[] bannedPhrases;

    private ServerSocket serverSocket;

    public Server() throws IOException {
        loadProperties();
        serverSocket = new ServerSocket(port);
    }

    private void loadProperties() {
        Properties properties = new Properties();
        String expectedFileLocation = "res/server.properties";
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(expectedFileLocation))) {
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
                properties.store(new OutputStreamWriter(new FileOutputStream(expectedFileLocation)), null);
            } catch (IOException e1) {
                throw new RuntimeException("Could not create template file");
            }
        }
    }
}
