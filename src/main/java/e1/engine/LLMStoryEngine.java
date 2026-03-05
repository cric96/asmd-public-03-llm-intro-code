package e1.engine;

import dev.langchain4j.model.chat.ChatModel;
import e1.model.StoryResponse;
import e1.prompt.StoryPrompt;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LLMStoryEngine implements StoryEngine {

    private static final Logger LOGGER = Logger.getLogger(LLMStoryEngine.class.getName());
    private static final int MAX_RETRIES = 3;
    private final ChatModel model;
    private final JsonCodec codec;
    private final int maxRetries;

    public LLMStoryEngine(ChatModel model, JsonCodec codec, int maxRetries) {
        this.model = Objects.requireNonNull(model);
        this.codec = Objects.requireNonNull(codec);
        this.maxRetries = maxRetries;
    }

    public LLMStoryEngine(ChatModel model) {
        this(model, new JsonCodec(), MAX_RETRIES);
    }

    @Override
    public StoryResponse request(StoryPrompt prompt) {
        String promptString = prompt.toPromptString();
        for (int attempt = 1; attempt <= this.maxRetries; attempt++) {
            try {
                LOGGER.log(Level.INFO, "Attempt {0}/{1}", new Object[]{attempt, this.maxRetries});
                String raw = model.chat(promptString);
                LOGGER.log(Level.INFO, "Raw LLM response: {0}", raw);
                return codec.decode(raw, StoryResponse.class);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Attempt {0} failed: {1}",
                    new Object[]{attempt, e.getMessage()});
            }
        }
        throw new IllegalStateException(
            "Failed to get a valid response after " + MAX_RETRIES + " attempts"
        );
    }
}
