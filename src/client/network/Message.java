package client.network;

public record Message(String message, String sender, MessageType type) {
    @Override
    public String toString() {
        if (sender.equals("SERVER")) {
            return message;
        }
        String prefix = type == MessageType.NORMAL ? "" : "PRIVATE ";
        return prefix + sender + ": " + message;
    }
}
