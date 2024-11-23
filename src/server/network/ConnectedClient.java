package server.network;

import java.io.BufferedReader;
import java.io.PrintWriter;

public record ConnectedClient(String ip, int port, String id, BufferedReader in, PrintWriter out) {

    @Override
    public String toString() {
        return id;
    }

    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }
}
