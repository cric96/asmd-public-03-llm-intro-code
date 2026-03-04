package it.unibo.tictactoe.model;

/**
 * Named constants for the Tic Tac Toe game.
 * Eliminates magic numbers throughout the codebase.
 */
public final class GameConstants {

    private GameConstants() {
        // Prevent instantiation
    }

    public static final int BOARD_SIZE = 3;
    public static final String EMPTY_CELL_SYMBOL = ".";
    public static final int WINNING_LINE_COUNT = BOARD_SIZE * 2 + 2;
    public static final int SEPARATOR_LENGTH = BOARD_SIZE * 4 - 3;
}
