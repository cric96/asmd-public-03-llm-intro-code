package it.unibo.tictactoe.controller.prompt;

import it.unibo.tictactoe.controller.formatter.BoardFormatter;
import it.unibo.tictactoe.controller.formatter.TextBoardFormatter;
import it.unibo.tictactoe.model.Board;
import it.unibo.tictactoe.model.Player;

import java.util.Objects;

import static it.unibo.tictactoe.model.GameConstants.*;

public final class JsonMovePrompt implements TicTacToePrompt {

    private static final String TEMPLATE = """
        You are playing Tic Tac Toe as player %s on a %dx%d board.
        Current board (rows 0-%d, columns 0-%d):
        %s
        Empty cells marked with '%s'. Available positions: %s.
        Respond with ONLY a JSON object: {"row": <r>, "col": <c>}. No explanation.""";

    private final Player aiPlayer;
    private final BoardFormatter formatter;

    public JsonMovePrompt(Player aiPlayer, BoardFormatter formatter) {
        this.aiPlayer = Objects.requireNonNull(aiPlayer);
        this.formatter = Objects.requireNonNull(formatter);
    }

    public JsonMovePrompt(Player aiPlayer) {
        this(aiPlayer, new TextBoardFormatter());
    }

    @Override
    public String toPromptString(Board board) {
        return String.format(TEMPLATE,
            aiPlayer.name(),
            BOARD_SIZE, BOARD_SIZE,
            BOARD_SIZE - 1, BOARD_SIZE - 1,
            formatter.format(board),
            EMPTY_CELL_SYMBOL,
            formatter.emptyCells(board)
        );
    }
}
