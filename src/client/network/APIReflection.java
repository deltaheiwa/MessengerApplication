package client.network;

import common.APISkeleton;

public class APIReflection implements APISkeleton {
    private final Client client;

    public APIReflection(Client client) {
        this.client = client;
    }

    @Override
    public void connect() throws Exception {

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
    public void disconnect() {

    }
}
