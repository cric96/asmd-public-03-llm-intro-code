package it.unibo.tictactoe.controller.prompt;

import it.unibo.tictactoe.model.Board;

public interface TicTacToePrompt {
    String toPromptString(Board board);
}
