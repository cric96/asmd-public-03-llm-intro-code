package it.unibo.tictactoe.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static it.unibo.tictactoe.model.GameConstants.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link BoardImpl}.
 */
class BoardImplTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new BoardImpl();
    }

    @Test
    void newBoardShouldHaveAllCellsEmpty() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                assertTrue(board.isEmpty(row, col),
                        String.format("Cell (%d, %d) should be empty", row, col));
                assertEquals(Optional.empty(), board.getCell(row, col));
            }
        }
    }

    @Test
    void newBoardShouldNotBeFull() {
        assertFalse(board.isFull());
    }

    @Test
    void setCellShouldPlacePlayerCorrectly() {
        board.setCell(0, 0, Player.X);
        assertEquals(Optional.of(Player.X), board.getCell(0, 0));
        assertFalse(board.isEmpty(0, 0));
    }

    @Test
    void setCellShouldPlaceBothPlayers() {
        board.setCell(0, 0, Player.X);
        board.setCell(1, 1, Player.O);
        assertEquals(Optional.of(Player.X), board.getCell(0, 0));
        assertEquals(Optional.of(Player.O), board.getCell(1, 1));
    }

    @Test
    void setCellShouldRejectOccupiedCell() {
        board.setCell(1, 1, Player.X);
        assertThrows(IllegalStateException.class, () -> board.setCell(1, 1, Player.O));
    }

    @Test
    void setCellShouldRejectSamePlayerOnOccupiedCell() {
        board.setCell(1, 1, Player.X);
        assertThrows(IllegalStateException.class, () -> board.setCell(1, 1, Player.X));
    }

    @Test
    void setCellShouldRejectNullPlayer() {
        assertThrows(NullPointerException.class, () -> board.setCell(0, 0, null));
    }

    @Test
    void getCellShouldRejectNegativeRow() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getCell(-1, 0));
    }

    @Test
    void getCellShouldRejectRowBeyondBoardSize() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getCell(BOARD_SIZE, 0));
    }

    @Test
    void getCellShouldRejectNegativeColumn() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getCell(0, -1));
    }

    @Test
    void getCellShouldRejectColumnBeyondBoardSize() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getCell(0, BOARD_SIZE));
    }

    @Test
    void setCellShouldRejectOutOfBoundsPositions() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> board.setCell(-1, 0, Player.X));
        assertThrows(IndexOutOfBoundsException.class,
                () -> board.setCell(0, BOARD_SIZE, Player.X));
    }

    @Test
    void isEmptyShouldRejectOutOfBoundsPositions() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.isEmpty(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> board.isEmpty(0, BOARD_SIZE));
    }

    @Test
    void boardShouldBeFullWhenAllCellsAreSet() {
        fillBoardWithDraw();
        assertTrue(board.isFull());
    }

    @Test
    void boardShouldNotBeFullWithOneCellRemaining() {
        // Fill all but (2,2)
        board.setCell(0, 0, Player.X);
        board.setCell(0, 1, Player.O);
        board.setCell(0, 2, Player.X);
        board.setCell(1, 0, Player.O);
        board.setCell(1, 1, Player.X);
        board.setCell(1, 2, Player.O);
        board.setCell(2, 0, Player.X);
        board.setCell(2, 1, Player.X);
        assertFalse(board.isFull());
    }

    @Test
    void toStringShouldContainPlayerSymbols() {
        board.setCell(0, 0, Player.X);
        board.setCell(1, 1, Player.O);
        String repr = board.toString();
        assertTrue(repr.contains("X"), "Board string should contain X");
        assertTrue(repr.contains("O"), "Board string should contain O");
    }

    @Test
    void toStringShouldContainEmptyCellSymbol() {
        String repr = board.toString();
        assertTrue(repr.contains(GameConstants.EMPTY_CELL_SYMBOL),
                "Empty board should contain the empty cell symbol");
    }

    @Test
    void settingCellShouldNotAffectOtherCells() {
        board.setCell(0, 0, Player.X);
        assertTrue(board.isEmpty(0, 1));
        assertTrue(board.isEmpty(1, 0));
        assertTrue(board.isEmpty(1, 1));
    }

    /**
     * Fills the board with a draw configuration:
     * X O X
     * O X O
     * X X O
     */
    private void fillBoardWithDraw() {
        board.setCell(0, 0, Player.X);
        board.setCell(0, 1, Player.O);
        board.setCell(0, 2, Player.X);
        board.setCell(1, 0, Player.O);
        board.setCell(1, 1, Player.X);
        board.setCell(1, 2, Player.O);
        board.setCell(2, 0, Player.X);
        board.setCell(2, 1, Player.X);
        board.setCell(2, 2, Player.O);
    }
}
