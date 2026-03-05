package e1.model;

import e1.engine.StoryEngine;
import e1.prompt.AdvancePrompt;
import e1.prompt.BeginPrompt;
import e1.prompt.StoryPrompt;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class StoryImpl implements Story {

    private final StoryEngine engine;
    private final String setting;
    private Player player;
    private StoryResponse currentBeat;

    public StoryImpl(StoryEngine engine, Player initialPlayer, String setting) {
        this.engine = Objects.requireNonNull(engine);
        this.player = Objects.requireNonNull(initialPlayer);
        this.setting = Objects.requireNonNull(setting);
        this.currentBeat = engine.request(new BeginPrompt(initialPlayer, setting));
        this.player = currentBeat.updatedPlayer();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public String narrative() {
        return currentBeat.narrative();
    }

    @Override
    public String currentQuestion() {
        return currentBeat.question();
    }

    @Override
    public List<String> choices() {
        return currentBeat.gameOver()
            ? Collections.emptyList()
            : Collections.unmodifiableList(currentBeat.choices());
    }

    @Override public boolean isGameOver() { return currentBeat.gameOver(); }

    @Override
    public void makeDecision(int decision) {
        if (isGameOver()) throw new IllegalStateException("The story is over.");
        final List<String> available = currentBeat.choices();
        if (decision < 0 || decision >= available.size()) {
            throw new IllegalArgumentException(
                    "Invalid choice: " + decision + " (valid: 0.." + (available.size() - 1) + ")"
            );
        }
        final String chosen = available.get(decision);
        final StoryPrompt prompt = new AdvancePrompt(player, currentBeat.question(), chosen);
        currentBeat = engine.request(prompt);
        player = currentBeat.updatedPlayer();
    }
}
