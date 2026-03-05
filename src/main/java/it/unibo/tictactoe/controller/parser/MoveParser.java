package it.unibo.tictactoe.controller.parser;

import it.unibo.utils.Pair;

import java.util.Optional;

public interface MoveParser {
    Optional<Pair<Integer, Integer>> parse(String response);
}
