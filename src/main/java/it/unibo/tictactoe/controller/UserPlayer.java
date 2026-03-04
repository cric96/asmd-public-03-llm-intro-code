package it.unibo.tictactoe.controller;

import it.unibo.tictactoe.model.Board;
import it.unibo.tictactoe.view.CellListener;
import it.unibo.utils.Pair;

import java.util.concurrent.*;

/**
 * Human player that receives moves from UI click events via a blocking queue.
 * Uses virtual threads to avoid blocking the calling thread while waiting for input.
 */
public class UserPlayer implements PlayerLogic, CellListener {
    private final BlockingQueue<Pair<Integer, Integer>> decision = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public CompletableFuture<Pair<Integer, Integer>> getNextMove(Board game) {
        return CompletableFuture.supplyAsync(this::blockingLogicWithDecision, executor);
    }

    @Override
    public void onCellClicked(int row, int col) {
        decision.add(new Pair<>(row, col));
    }

    private Pair<Integer, Integer> blockingLogicWithDecision() {
        try {
            return decision.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new Pair<>(0, 0);
    }
}
