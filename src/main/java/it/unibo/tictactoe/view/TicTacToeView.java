package it.unibo.tictactoe.view;

public interface TicTacToeView {
    BoardView view();
    void attachListener(CellListener listener);
}
