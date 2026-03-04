package it.unibo.tictactoe.controller;

import dev.langchain4j.model.chat.ChatModel;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.unibo.tictactoe.model.GameConstants.*;

/**
 * AI player that uses a Large Language Model (via LangChain4j) to decide its moves.
 * Falls back to the first available empty cell after {@link #MAX_RETRIES} failed attempts.
 *
 * <p>Responsibilities are split into inner collaborators:
 * <ul>
 *   <li>{@link MoveParser} — extracts a row,col pair from LLM text</li>
 *   <li>{@link PromptBuilder} — assembles the prompt string for a given board state</li>
 *   <li>{@link BoardFormatter} — renders the board as a human-readable string</li>
 * </ul>
 */
public class AIPlayer implements PlayerLogic {

    private static final Logger LOGGER = Logger.getLogger(AIPlayer.class.getName());
    public static final double RECOMMENDED_TEMPERATURE = 0.3;
    private static final int MAX_RETRIES = 5;
    private final ChatModel model;
    private final Player aiPlayer;
    private final ExecutorService executor;
    private final MoveParser moveParser;
    private final PromptBuilder promptBuilder;

    /**
     * Creates an AI player backed by the given chat model.
     *
     * @param model    the LLM chat model (must not be null)
     * @param aiPlayer which player mark this AI controls (must not be null)
     */
    public AIPlayer(ChatModel model, Player aiPlayer) {
        this.model = Objects.requireNonNull(model, "ChatModel must not be null");
        this.aiPlayer = Objects.requireNonNull(aiPlayer, "Player must not be null");
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.moveParser = new MoveParser();
        this.promptBuilder = new PromptBuilder(aiPlayer);
    }

    @Override
    public CompletableFuture<Pair<Integer, Integer>> getNextMove(Board board) {
        return CompletableFuture.supplyAsync(() -> computeMove(board), executor);
    }

    private Pair<Integer, Integer> computeMove(Board board) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            LOGGER.log(Level.INFO, "AI attempt {0}/{1}", new Object[]{attempt, MAX_RETRIES});
            String prompt = promptBuilder.build(board);
            LOGGER.log(Level.FINE, "Prompt sent to model:\n{0}", prompt);
            String response = model.chat(prompt);
            LOGGER.log(Level.INFO, "LLM response: {0}", response);
            Optional<Pair<Integer, Integer>> parsed = moveParser.parse(response);
            if (parsed.isPresent() && isValidMove(board, parsed.get())) {
                LOGGER.log(Level.INFO, "Accepted move: {0}", parsed.get());
                return parsed.get();
            }
            LOGGER.log(Level.WARNING, "Invalid or unparseable move on attempt {0}: {1}",
                    new Object[]{attempt, parsed});
        }
        Pair<Integer, Integer> fallback = findFirstEmptyCell(board)
            .orElseThrow(() -> new IllegalStateException("No empty cells available on board"));
        LOGGER.log(Level.WARNING, "All {0} attempts failed — falling back to {1}",
                new Object[]{MAX_RETRIES, fallback});
        return fallback;
    }

    private static boolean isValidMove(Board board, Pair<Integer, Integer> move) {
        int row = move.x();
        int col = move.y();
        return row >= 0 && row < BOARD_SIZE
                && col >= 0 && col < BOARD_SIZE
                && board.isEmpty(row, col);
    }

    private static Optional<Pair<Integer, Integer>> findFirstEmptyCell(Board board) {
        return IntStream.range(0, BOARD_SIZE)
            .boxed()
            .flatMap(row -> IntStream.range(0, BOARD_SIZE)
                .filter(col -> board.isEmpty(row, col))
                .mapToObj(col -> Pair.of(row, col)))
            .findFirst();
    }

    /**
     * Parses a {@code row,col} move from free-form LLM text.
     */
    static final class MoveParser {

        private static final Pattern MOVE_PATTERN =
            Pattern.compile("(\\d)\\s*[,\\s]\\s*(\\d)");

        /**
         * Attempts to extract the first {@code row,col} pair from the given text.
         *
         * @param response the LLM response text
         * @return the parsed move, or empty if none found
         */
        public Optional<Pair<Integer, Integer>> parse(String response) {
            Matcher matcher = MOVE_PATTERN.matcher(response);
            if (matcher.find()) {
                int row = Integer.parseInt(matcher.group(1));
                int col = Integer.parseInt(matcher.group(2));
                return Optional.of(Pair.of(row, col));
            }
            return Optional.empty();
        }
    }

    /**
     * Builds the text prompt sent to the LLM for a given board state.
     */
    static final class PromptBuilder {

        private final Player aiPlayer;

        PromptBuilder(Player aiPlayer) {
            this.aiPlayer = Objects.requireNonNull(aiPlayer);
        }

        /**
         * Assembles a complete prompt describing the board and requesting a move.
         *
         * @param board the current board state
         * @return the prompt string
         */
        String build(Board board) {
            String boardText = BoardFormatter.format(board);
            String available = BoardFormatter.emptyCellDescriptions(board);
            return String.format(
                "You are playing Tic Tac Toe as player %s on a %dx%d board.%n"
                    + "Current board (rows 0-%d, columns 0-%d):%n%s%n"
                    + "Empty cells marked with '%s'. Available positions: %s.%n"
                    + "Respond with ONLY row,column (e.g. 1,2). No explanation.",
                aiPlayer.name(),
                BOARD_SIZE,
                BOARD_SIZE,
                BOARD_SIZE - 1,
                BOARD_SIZE - 1,
                boardText,
                EMPTY_CELL_SYMBOL,
                available
            );
        }
    }

    static final class BoardFormatter {

        private BoardFormatter() {
            // utility class
        }

        /**
         * Formats the board as a grid with row separators.
         *
         * @param board the board to format
         * @return multi-line string representation
         */
        static String format(Board board) {
            String separator = "-".repeat(SEPARATOR_LENGTH);
            return IntStream.range(0, BOARD_SIZE)
                .mapToObj(row -> IntStream.range(0, BOARD_SIZE)
                    .mapToObj(col -> board.getCell(row, col)
                        .map(Player::name)
                        .orElse(EMPTY_CELL_SYMBOL))
                    .collect(Collectors.joining(" | ")))
                .collect(
                    Collectors.joining(System.lineSeparator() + separator + System.lineSeparator())
                );
        }

        static String emptyCellDescriptions(Board board) {
            return IntStream.range(0, BOARD_SIZE)
                .boxed()
                .flatMap(row -> IntStream.range(0, BOARD_SIZE)
                    .filter(col -> board.isEmpty(row, col))
                    .mapToObj(col -> String.format("(%d,%d)", row, col)))
                .collect(Collectors.joining(", "));
        }
    }
}
