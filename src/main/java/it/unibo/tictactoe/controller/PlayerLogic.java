package it.unibo.tictactoe.controller;

import it.unibo.tictactoe.model.Board;
import it.unibo.utils.Pair;

import java.util.concurrent.CompletableFuture;

public interface PlayerLogic {
    CompletableFuture<Pair<Integer, Integer>> getNextMove(Board game);
}
