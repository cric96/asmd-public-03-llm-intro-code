package it.unibo.tictactoe.controller.formatter;

import it.unibo.tictactoe.model.Board;

public interface BoardFormatter {
    String format(Board board);
    String emptyCells(Board board);
}
