package e1.prompt;

import e1.model.Player;

public record AdvancePrompt(
        Player player,
        String previousQuestion,
        String chosenAction
) implements StoryPrompt {

    private static final String TEMPLATE = """
        You are a text-adventure game master continuing an interactive story.

        Player: %s | health=%d | attackPower=%d
        Previous situation: %s
        Player chose: %s

        Advance the story. The choice may affect health or attackPower.
        If health reaches 0 or below, set gameOver to true.

        Respond with ONLY a JSON object (no markdown, no extra text):
        {
          "narrative": "What happens next",
          "question": "The next decision",
          "choices": ["Choice 1", "Choice 2", "Choice 3"],
          "updatedPlayer": {"name": "%s", "health": <updated>, "attackPower": <updated>},
          "gameOver": <true_or_false>
        }
        """;

    @Override
    public String toPromptString() {
        return String.format(TEMPLATE,
            player.name(), player.health(), player.attackPower(),
            previousQuestion, chosenAction,
            player.name()
        );
    }
}
