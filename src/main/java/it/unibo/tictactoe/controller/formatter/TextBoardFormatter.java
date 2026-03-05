package it.unibo.tictactoe.controller.formatter;

import it.unibo.tictactoe.model.Board;
import it.unibo.tictactoe.model.Player;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.unibo.tictactoe.model.GameConstants.*;

public final class TextBoardFormatter implements BoardFormatter {

    @Override
    public String format(Board board) {
        String separator = "-".repeat(SEPARATOR_LENGTH);
        return IntStream.range(0, BOARD_SIZE)
            .mapToObj(row -> IntStream.range(0, BOARD_SIZE)
                .mapToObj(col -> board.getCell(row, col)
                    .map(Player::name)
                    .orElse(EMPTY_CELL_SYMBOL))
                .collect(Collectors.joining(" | ")))
            .collect(Collectors.joining(
                System.lineSeparator() + separator + System.lineSeparator()));
    }

    @Override
    public String emptyCells(Board board) {
        return IntStream.range(0, BOARD_SIZE).boxed()
            .flatMap(row -> IntStream.range(0, BOARD_SIZE)
                .filter(col -> board.isEmpty(row, col))
                .mapToObj(col -> String.format("(%d,%d)", row, col)))
            .collect(Collectors.joining(", "));
    }
}
