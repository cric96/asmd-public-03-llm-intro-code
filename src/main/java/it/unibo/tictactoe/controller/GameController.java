package it.unibo.tictactoe.controller;

import it.unibo.tictactoe.model.Player;
import it.unibo.tictactoe.model.TicTacToe;
import it.unibo.tictactoe.view.TicTacToeView;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Orchestrates the Tic Tac Toe game loop, alternating between two {@link PlayerLogic}
 * instances and updating the {@link TicTacToeView}.
 * The game loop runs on a virtual thread to avoid blocking the UI.
 */
public class GameController {
    private final TicTacToe game;
    private final Map<Player, PlayerLogic> players;
    private final TicTacToeView view;
    private final Consumer<String> messageHandler;
    private final ExecutorService executor;
    private volatile Player currentPlayer;
    /**
     * Creates a game controller.
     *
     * @param game           the game model
     * @param playerX        logic for player X (first mover)
     * @param playerO        logic for player O
     * @param view           the game view
     * @param messageHandler callback for game-over messages
     */
    public GameController(
        TicTacToe game,
        PlayerLogic playerX,
        PlayerLogic playerO,
        TicTacToeView view,
        Consumer<String> messageHandler
    ) {
        this.game = Objects.requireNonNull(game, "Game must not be null");
        this.players = Map.of(
            Player.X, Objects.requireNonNull(playerX, "PlayerX must not be null"),
            Player.O, Objects.requireNonNull(playerO, "PlayerO must not be null")
        );
        this.view = Objects.requireNonNull(view, "View must not be null");
        this.messageHandler = Objects.requireNonNull(messageHandler, "MessageHandler must not be null");
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.currentPlayer = Player.X;
    }

    /**
     * Starts the game: attaches the controller as a click listener, renders the
     * initial board, and begins the game loop on a virtual thread.
     *
     * @return a {@link Future} that completes when the game ends
     */
    public Future<?> startGame() {
        view.view().renderBoard(game.getBoard());
        return executor.submit(this::gameLoop);
    }

    private void gameLoop() {
        if(!game.isGameOver()) {
            PlayerLogic current = players.get(currentPlayer);
            current.getNextMove(game.getBoard()).thenAccept(move -> {
                game.getBoard().setCell(move.x(), move.y(), currentPlayer);
                view.view().renderBoard(game.getBoard());
                currentPlayer = nextPlayer();
                gameLoop();
            });
        } else {
            announceResult();
        }
    }

    private Player nextPlayer() {
        return currentPlayer == Player.X ? Player.O : Player.X;
    }

    private void announceResult() {
        final String message = game.getWinner()
            .map(winner -> winner.name() + " wins!")
            .orElse("It's a draw!");
        messageHandler.accept(message);
    }
}
