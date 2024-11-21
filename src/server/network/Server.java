package server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

public class Server {
    private final int port = 8790;

    private ServerSocket serverSocket;

    public Server() throws IOException {
        serverSocket = new ServerSocket(port);
    }

    private void loadProperties() {
        // TODO
    }
}
