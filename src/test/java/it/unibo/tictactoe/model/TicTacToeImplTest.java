package it.unibo.tictactoe.model;

import it.unibo.utils.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static it.unibo.tictactoe.model.GameConstants.BOARD_SIZE;
import static it.unibo.tictactoe.model.GameConstants.WINNING_LINE_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link TicTacToeImpl}.
 */
class TicTacToeImplTest {

    private TicTacToe game;

    @BeforeEach
    void setUp() {
        game = new TicTacToeImpl();
    }

    @Test
    void newGameShouldNotBeOver() {
        assertFalse(game.isGameOver());
    }

    @Test
    void newGameShouldHaveNoWinner() {
        assertEquals(Optional.empty(), game.getWinner());
    }

    @Test
    void gameShouldNotBeOverAfterOneMove() {
        game.getBoard().setCell(0, 0, Player.X);
        assertFalse(game.isGameOver());
        assertEquals(Optional.empty(), game.getWinner());
    }

    // --- Row wins ---
    @Test
    void xShouldWinWithTopRow() {
        game.getBoard().setCell(0, 0, Player.X);
        game.getBoard().setCell(1, 0, Player.O);
        game.getBoard().setCell(0, 1, Player.X);
        game.getBoard().setCell(1, 1, Player.O);
        game.getBoard().setCell(0, 2, Player.X);

        assertTrue(game.isGameOver());
        assertEquals(Optional.of(Player.X), game.getWinner());
    }

    @Test
    void oShouldWinWithMiddleRow() {
        game.getBoard().setCell(0, 0, Player.X);
        game.getBoard().setCell(1, 0, Player.O);
        game.getBoard().setCell(0, 1, Player.X);
        game.getBoard().setCell(1, 1, Player.O);
        game.getBoard().setCell(2, 2, Player.X);
        game.getBoard().setCell(1, 2, Player.O);

        assertTrue(game.isGameOver());
        assertEquals(Optional.of(Player.O), game.getWinner());
    }

    @Test
    void xShouldWinWithBottomRow() {
        game.getBoard().setCell(2, 0, Player.X);
        game.getBoard().setCell(0, 0, Player.O);
        game.getBoard().setCell(2, 1, Player.X);
        game.getBoard().setCell(0, 1, Player.O);
        game.getBoard().setCell(2, 2, Player.X);
        assertTrue(game.isGameOver());
        assertEquals(Optional.of(Player.X), game.getWinner());
    }

    // --- Column wins ---
    @Test
    void xShouldWinWithLeftColumn() {
        game.getBoard().setCell(0, 0, Player.X);
        game.getBoard().setCell(0, 1, Player.O);
        game.getBoard().setCell(1, 0, Player.X);
        game.getBoard().setCell(1, 1, Player.O);
        game.getBoard().setCell(2, 0, Player.X);
        assertTrue(game.isGameOver());
        assertEquals(Optional.of(Player.X), game.getWinner());
    }

    @Test
    void oShouldWinWithMiddleColumn() {
        game.getBoard().setCell(0, 0, Player.X);
        game.getBoard().setCell(0, 1, Player.O);
        game.getBoard().setCell(1, 2, Player.X);
        game.getBoard().setCell(1, 1, Player.O);
        game.getBoard().setCell(2, 0, Player.X);
        game.getBoard().setCell(2, 1, Player.O);
        assertTrue(game.isGameOver());
        assertEquals(Optional.of(Player.O), game.getWinner());
    }

    @Test
    void xShouldWinWithRightColumn() {
        game.getBoard().setCell(0, 2, Player.X);
        game.getBoard().setCell(0, 0, Player.O);
        game.getBoard().setCell(1, 2, Player.X);
        game.getBoard().setCell(1, 0, Player.O);
        game.getBoard().setCell(2, 2, Player.X);
        assertTrue(game.isGameOver());
        assertEquals(Optional.of(Player.X), game.getWinner());
    }

    // --- Diagonal wins ---
    @Test
    void xShouldWinWithMainDiagonal() {
        game.getBoard().setCell(0, 0, Player.X);
        game.getBoard().setCell(1, 0, Player.O);
        game.getBoard().setCell(1, 1, Player.X);
        game.getBoard().setCell(2, 0, Player.O);
        game.getBoard().setCell(2, 2, Player.X);
        assertTrue(game.isGameOver());
        assertEquals(Optional.of(Player.X), game.getWinner());
    }

    @Test
    void oShouldWinWithAntiDiagonal() {
        game.getBoard().setCell(0, 0, Player.X);
        game.getBoard().setCell(0, 2, Player.O);
        game.getBoard().setCell(1, 0, Player.X);
        game.getBoard().setCell(1, 1, Player.O);
        game.getBoard().setCell(2, 2, Player.X);
        game.getBoard().setCell(2, 0, Player.O);
        assertTrue(game.isGameOver());
        assertEquals(Optional.of(Player.O), game.getWinner());
    }

    // --- Draw ---
    @Test
    void drawGameShouldBeOverWithNoWinner() {
        // X O X
        // X X O
        // O X O
        game.getBoard().setCell(0, 0, Player.X);
        game.getBoard().setCell(0, 1, Player.O);
        game.getBoard().setCell(0, 2, Player.X);
        game.getBoard().setCell(1, 0, Player.X);
        game.getBoard().setCell(1, 1, Player.X);
        game.getBoard().setCell(1, 2, Player.O);
        game.getBoard().setCell(2, 0, Player.O);
        game.getBoard().setCell(2, 1, Player.X);
        game.getBoard().setCell(2, 2, Player.O);
        assertTrue(game.isGameOver());
        assertEquals(Optional.empty(), game.getWinner());
    }

    // --- Winning lines structure ---
    @Test
    void winningLinesShouldHaveCorrectCount() {
        List<List<Pair<Integer, Integer>>> lines = TicTacToeImpl.getWinningLines();
        assertEquals(WINNING_LINE_COUNT, lines.size());
    }

    @Test
    void winningLinesShouldBeUnmodifiable() {
        List<List<Pair<Integer, Integer>>> lines = TicTacToeImpl.getWinningLines();
        assertThrows(UnsupportedOperationException.class, () -> lines.add(List.of()));
    }

    @Test
    void eachWinningLineShouldHaveBoardSizePositions() {
        TicTacToeImpl.getWinningLines()
            .forEach(line -> assertEquals(
                BOARD_SIZE, 
                line.size(),
        "Each winning line should contain exactly BOARD_SIZE positions")
        );
    }

    @Test
    void allWinningLinePositionsShouldBeInBounds() {
        TicTacToeImpl.getWinningLines().stream()
            .flatMap(List::stream)
            .forEach(pos -> {
                assertTrue(
                    pos.x() >= 0 && pos.x() < BOARD_SIZE,
                "Row should be in bounds: " + pos
                );
                assertTrue(
                    pos.y() >= 0 && pos.y() < BOARD_SIZE,
                    "Col should be in bounds: " + pos
                );
            });
    }
}
