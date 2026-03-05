package e1.engine;

import e1.model.StoryResponse;
import e1.prompt.StoryPrompt;

public interface StoryEngine {
    StoryResponse request(StoryPrompt prompt);
}
