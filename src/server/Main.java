package server;

import server.network.Server;

public class Main {
    public static void main(String[] args) {
        try {
            Server server = new Server();
        } catch (Exception e) {
            System.out.println("Failed to start server");
            e.printStackTrace();
        }
    }
}