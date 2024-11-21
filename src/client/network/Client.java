package client.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private BufferedReader in;
    private PrintWriter out;

    public Client() throws IOException {
        Socket socket = new Socket("localhost", 8790);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream());
    }

    public boolean sendUsernameToServer(String username) {
        out.println(username);
        out.flush();
        return true;
    }
}
