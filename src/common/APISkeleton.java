package common;

public interface APISkeleton {
    void connect() throws Exception;

    int sendMessage(String m) throws Exception;

    int sendMessageToSpecificClient(String m, int clientID) throws Exception;

    int sendMessageToMultipleClients(String m, int[] clientIDs) throws Exception;

    int sendMessageExcept(String m, int[] clientIDs) throws Exception;

    String[] getBannedPhrases() throws Exception;

    void disconnect();
}
