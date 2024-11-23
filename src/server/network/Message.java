package server.network;

public record Message(String content, String sender, String[] recipients, boolean areExcluded, boolean isBanned, String bannedPhrase) {
    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public String[] getRecipients() {
        return recipients;
    }

    public boolean areExcluded() {
        return areExcluded;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public String getBannedPhrase() {
        return bannedPhrase;
    }
}
