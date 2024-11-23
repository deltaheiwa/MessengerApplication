package client.ui;

public enum Screen {
    USERNAME_PROMPT(new UsernamePrompt()),
    CHAT(new Chat());

    private final Scene panel;

    Screen(Scene panel) {
        this.panel = panel;
    }

    public Scene getPanel() {
        return panel;
    }
}
