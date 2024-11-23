package common;

public interface APISkeleton {
    boolean connect(String username) throws Exception;

    void sendMessage(String m, String sender) throws Exception;

    String[] getBannedPhrases() throws Exception;

    void getConnectedClients() throws Exception;

    void disconnect();
}
