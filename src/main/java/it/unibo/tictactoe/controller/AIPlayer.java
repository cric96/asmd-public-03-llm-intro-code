package it.unibo.tictactoe.controller;

import dev.langchain4j.model.chat.ChatModel;
import it.unibo.tictactoe.controller.formatter.BoardFormatter;
import it.unibo.tictactoe.controller.parser.GsonMoveParser;
import it.unibo.tictactoe.controller.parser.MoveParser;
import it.unibo.tictactoe.controller.prompt.JsonMovePrompt;
import it.unibo.tictactoe.controller.prompt.TicTacToePrompt;
import it.unibo.tictactoe.model.Board;
import it.unibo.tictactoe.model.Player;
import it.unibo.utils.Pair;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static it.unibo.tictactoe.model.GameConstants.*;

/**
 * AI player that uses an LLM (via LangChain4j) to decide its moves.
 * Falls back to the first available empty cell after {@link #MAX_RETRIES} failed attempts.
 * <p>
 * Depends on interfaces only (DIP):
 * <ul>
 *   <li>{@link TicTacToePrompt} — builds the prompt for the LLM</li>
 *   <li>{@link MoveParser} — extracts a move from the LLM response</li>
 * </ul>
 */
public class AIPlayer implements PlayerLogic {

    private static final Logger LOGGER = Logger.getLogger(AIPlayer.class.getName());
    public static final double RECOMMENDED_TEMPERATURE = 0.3;
    private static final int MAX_RETRIES = 5;

    private final ChatModel model;
    private final TicTacToePrompt prompt;
    private final MoveParser parser;
    private final ExecutorService executor;

    /**
     * Full constructor — inject all collaborators.
     */
    public AIPlayer(ChatModel model, TicTacToePrompt prompt, MoveParser parser) {
        this.model = Objects.requireNonNull(model);
        this.prompt = Objects.requireNonNull(prompt);
        this.parser = Objects.requireNonNull(parser);
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * Convenience constructor — defaults to Gson-based prompt and parser.
     */
    public AIPlayer(ChatModel model, Player aiPlayer) {
        this(model, new JsonMovePrompt(aiPlayer), new GsonMoveParser());
    }

    @Override
    public CompletableFuture<Pair<Integer, Integer>> getNextMove(Board board) {
        return CompletableFuture.supplyAsync(() -> computeMove(board), executor);
    }

    private Pair<Integer, Integer> computeMove(Board board) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            LOGGER.log(Level.INFO, "AI attempt {0}/{1}", new Object[]{attempt, MAX_RETRIES});
            String promptText = prompt.toPromptString(board);
            String response = model.chat(promptText);
            LOGGER.log(Level.INFO, "LLM response: {0}", response);
            Optional<Pair<Integer, Integer>> parsed = parser.parse(response);
            if (parsed.isPresent() && isValidMove(board, parsed.get())) {
                LOGGER.log(Level.INFO, "Accepted move: {0}", parsed.get());
                return parsed.get();
            }
            LOGGER.log(Level.WARNING, "Invalid or unparseable move on attempt {0}: {1}",
                new Object[]{attempt, parsed});
        }
        Pair<Integer, Integer> fallback = findFirstEmptyCell(board)
            .orElseThrow(() -> new IllegalStateException("No empty cells available"));
        LOGGER.log(Level.WARNING, "All {0} attempts failed — falling back to {1}",
            new Object[]{MAX_RETRIES, fallback});
        return fallback;
    }

    private static boolean isValidMove(Board board, Pair<Integer, Integer> move) {
        int row = move.x(), col = move.y();
        return row >= 0 && row < BOARD_SIZE
            && col >= 0 && col < BOARD_SIZE
            && board.isEmpty(row, col);
    }

    private static Optional<Pair<Integer, Integer>> findFirstEmptyCell(Board board) {
        return IntStream.range(0, BOARD_SIZE).boxed()
            .flatMap(row -> IntStream.range(0, BOARD_SIZE)
                .filter(col -> board.isEmpty(row, col))
                .mapToObj(col -> Pair.of(row, col)))
            .findFirst();
    }
}
