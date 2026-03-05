package e1;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import e1.engine.LLMStoryEngine;
import e1.model.Player;
import e1.model.Story;
import e1.model.StoryImpl;
import e1.view.ConsoleStoryView;
import e1.view.StoryView;
import it.unibo.utils.LlmConstants;

public final class StoryApp {

    private StoryApp() { }

    public static void main(String[] args) {
        final ChatModel model = OllamaChatModel.builder()
            .baseUrl(LlmConstants.OLLAMA_BASE_URL)
            .modelName(LlmConstants.CHAT_MODEL_QWEN)
            .temperature(1.0)
            .build();
        final ChatModel chatModelWithGemini = GoogleAiGeminiChatModel.builder()
            .apiKey(System.getenv("GEMINI_AI_KEY"))
            .modelName("gemini-3.1-flash-lite-preview")
            .build();
        final Story story = new StoryImpl(
            new LLMStoryEngine(chatModelWithGemini),
            new Player("Aria", 100, 15),
            "A mysterious dungeon beneath an ancient castle"
        );
        final StoryView view = new ConsoleStoryView();
        gameLoop(story, view);
    }

    static void gameLoop(Story story, StoryView view) {
        while (!story.isGameOver()) {
            view.showBeat(
                story.narrative(),
                story.getPlayer(),
                story.currentQuestion(),
                story.choices()
            );
            final int choice = view.readChoice(story.choices().size());
            story.makeDecision(choice);
        }
        view.showBeat(story.narrative(), story.getPlayer(), story.currentQuestion(), story.choices());
        view.showGameOver(story.getPlayer());
    }
}
