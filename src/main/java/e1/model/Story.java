package e1.model;

import java.util.List;

/**
 * Model interface for the interactive story.
 */
public interface Story {
    Player getPlayer();
    String narrative();
    String currentQuestion();
    List<String> choices();
    boolean isGameOver();
    void makeDecision(int decision);
}
