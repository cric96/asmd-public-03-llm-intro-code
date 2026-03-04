package it.unibo.tictactoe.model;

import it.unibo.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import static it.unibo.tictactoe.model.GameConstants.BOARD_SIZE;

/**
 * Implementation of {@link TicTacToe} with functional winner detection.
 * Winning lines are generated programmatically from {@link GameConstants#BOARD_SIZE}.
 */
public final class TicTacToeImpl implements TicTacToe {

    private final Board board;
    private static final List<List<Pair<Integer, Integer>>> WINNING_LINES = generateWinningLines();

    /**
     * Creates a new game with a fresh empty board.
     */
    public TicTacToeImpl() {
        this(new BoardImpl());
    }

    /**
     * Creates a new game wrapping the given board.
     *
     * @param board the board to use (must not be null)
     */
    public TicTacToeImpl(Board board) {
        this.board = Objects.requireNonNull(board, "Board must not be null");
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public boolean isGameOver() {
        return getWinner().isPresent() || board.isFull();
    }

    @Override
    public Optional<Player> getWinner() {
        return WINNING_LINES.stream()
            .map(this::checkLine)
            .flatMap(Optional::stream)
            .findFirst();
    }

    /**
     * Returns an unmodifiable list of all winning lines for the current board size.
     * Each line is a list of {@code (row, col)} positions.
     *
     * @return the winning lines
     */
    public static List<List<Pair<Integer, Integer>>> getWinningLines() {
        return WINNING_LINES;
    }

    private Optional<Player> checkLine(List<Pair<Integer, Integer>> line) {
        List<Optional<Player>> lineCells = line.stream()
            .map(pos -> board.getCell(pos.x(), pos.y()))
            .toList();
        final boolean allPresent = lineCells.stream().allMatch(Optional::isPresent);
        if (!allPresent) {
            return Optional.empty();
        } else {
            boolean allSame = lineCells.stream()
                .map(Optional::get)
                .distinct()
                .count() == 1;
            return allSame ? lineCells.getFirst() : Optional.empty();
        }

    }

    private static List<List<Pair<Integer, Integer>>> generateWinningLines() {
        List<List<Pair<Integer, Integer>>> lines = new ArrayList<>();
        // Rows
        IntStream.range(0, BOARD_SIZE).forEach(row ->
            lines.add(IntStream.range(0, BOARD_SIZE)
                .mapToObj(col -> Pair.of(row, col))
                .toList()
            )
        );
        // Columns
        IntStream.range(0, BOARD_SIZE).forEach(col ->
            lines.add(IntStream.range(0, BOARD_SIZE)
                .mapToObj(row -> Pair.of(row, col))
                .toList()
            )
        );
        // Main diagonal (top-left to bottom-right)
        lines.add(IntStream.range(0, BOARD_SIZE)
                .mapToObj(i -> Pair.of(i, i))
                .toList());
        // Anti-diagonal (top-right to bottom-left)
        lines.add(IntStream.range(0, BOARD_SIZE)
                .mapToObj(i -> Pair.of(i, BOARD_SIZE - 1 - i))
                .toList());
        return Collections.unmodifiableList(lines);
    }
}
