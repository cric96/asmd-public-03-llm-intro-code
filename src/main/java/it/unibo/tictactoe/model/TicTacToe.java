package it.unibo.tictactoe.model;

import java.util.Optional;

public interface TicTacToe {
    Board getBoard();
    boolean isGameOver();
    Optional<Player> getWinner();
}
