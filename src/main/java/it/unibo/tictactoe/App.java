package it.unibo.tictactoe;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import it.unibo.tictactoe.controller.AIPlayer;
import it.unibo.tictactoe.controller.GameController;
import it.unibo.tictactoe.controller.UserPlayer;
import it.unibo.tictactoe.model.Player;
import it.unibo.tictactoe.model.TicTacToe;
import it.unibo.tictactoe.model.TicTacToeImpl;
import it.unibo.tictactoe.view.SwingTicTacToeView;
import it.unibo.utils.LlmConstants;

/**
 * Entry point for the Tic Tac Toe application.
 * Human plays as X (first mover), LLM-powered AI plays as O.
 */
public class App {

    public static void main(String[] args) {
        ChatModel chatModel = OllamaChatModel.builder()
            .baseUrl(LlmConstants.OLLAMA_BASE_URL)
            .modelName(LlmConstants.CHAT_MODEL_QWEN)
            .temperature(AIPlayer.RECOMMENDED_TEMPERATURE)
            .build();
        ChatModel chatModelWithGemini = GoogleAiGeminiChatModel.builder()
            .apiKey(System.getenv("GEMINI_AI_KEY"))
            .modelName("gemini-3.1-flash-lite-preview")
            .build();
        TicTacToe game = new TicTacToeImpl();
        UserPlayer humanPlayer = new UserPlayer();
        AIPlayer aiPlayer = new AIPlayer(chatModelWithGemini, Player.O);
        SwingTicTacToeView view = new SwingTicTacToeView();
        view.attachListener(humanPlayer);
        GameController controller = new GameController(
            game, humanPlayer, aiPlayer, view, view::showMessage
        );
        controller.startGame();
    }
}
