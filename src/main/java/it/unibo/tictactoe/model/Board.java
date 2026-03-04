package it.unibo.tictactoe.model;

import java.util.Optional;

public interface Board {
    void setCell(int row, int col, Player value);
    Optional<Player> getCell(int row, int col);
    boolean isFull();
    boolean isEmpty(int row, int col);
}
