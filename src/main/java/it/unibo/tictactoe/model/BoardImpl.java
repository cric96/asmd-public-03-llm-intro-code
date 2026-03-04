package it.unibo.tictactoe.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;
import java.util.stream.IntStream;

import static it.unibo.tictactoe.model.GameConstants.BOARD_SIZE;
import static it.unibo.tictactoe.model.GameConstants.EMPTY_CELL_SYMBOL;
import static it.unibo.tictactoe.model.GameConstants.SEPARATOR_LENGTH;

/**
 * List-based implementation of {@link Board}.
 * Uses {@code List<List<Optional<Player>>>} internally — no arrays.
 */
public final class BoardImpl implements Board {

    private final List<List<Optional<Player>>> cells;

    /**
     * Creates a new empty board of size {@link GameConstants#BOARD_SIZE}.
     */
    public BoardImpl() {
        this.cells = IntStream.range(0, BOARD_SIZE)
            .mapToObj(row -> IntStream.range(0, BOARD_SIZE)
                .<Optional<Player>>mapToObj(col -> Optional.empty())
                .collect(Collectors.toCollection(ArrayList::new)))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void setCell(int row, int col, Player value) {
        validateBounds(row, col);
        Objects.requireNonNull(value, "Player value must not be null");
        if (!isEmpty(row, col)) {
            throw new IllegalStateException(
                String.format("Cell (%d, %d) is already occupied", row, col)
            );
        }
        cells.get(row).set(col, Optional.of(value));
    }

    @Override
    public Optional<Player> getCell(int row, int col) {
        validateBounds(row, col);
        return cells.get(row).get(col);
    }

    @Override
    public boolean isFull() {
        return cells.stream()
            .flatMap(Collection::stream)
            .allMatch(Optional::isPresent);
    }

    @Override
    public boolean isEmpty(int row, int col) {
        validateBounds(row, col);
        return cells.get(row).get(col).isEmpty();
    }

    @Override
    public String toString() {
        String separator = "-".repeat(SEPARATOR_LENGTH);
        return cells.stream()
            .map(row -> row.stream()
                .map(cell -> cell.map(Player::name).orElse(EMPTY_CELL_SYMBOL))
                .collect(Collectors.joining(" | ")))
            .collect(Collectors.joining(System.lineSeparator() + separator + System.lineSeparator()));
    }

    private static void validateBounds(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IndexOutOfBoundsException(
                String.format(
                    "Position (%d, %d) is out of bounds for board size %d", 
                    row, col, BOARD_SIZE
                )
            );
        }
    }
}
