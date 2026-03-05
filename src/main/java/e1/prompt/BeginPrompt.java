package e1.prompt;

import e1.model.Player;

public record BeginPrompt(Player player, String setting) implements StoryPrompt {

    private static final String TEMPLATE = """
        You are a text-adventure game master. Create the opening scene of an interactive story.

        Player: %s | health=%d | attackPower=%d
        Setting: %s

        Respond with ONLY a JSON object (no markdown, no extra text):
        {
          "narrative": "A short paragraph describing the scene",
          "question": "What does the player face?",
          "choices": ["Choice 1", "Choice 2", "Choice 3"],
          "updatedPlayer": {"name": "%s", "health": %d, "attackPower": %d},
          "gameOver": false
        }
        """;

    @Override
    public String toPromptString() {
        return String.format(TEMPLATE,
            player.name(), player.health(), player.attackPower(),
            setting,
            player.name(), player.health(), player.attackPower()
        );
    }
}
