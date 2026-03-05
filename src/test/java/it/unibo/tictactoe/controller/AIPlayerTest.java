package it.unibo.tictactoe.controller;

import dev.langchain4j.model.chat.ChatModel;
import it.unibo.tictactoe.controller.parser.GsonMoveParser;
import it.unibo.tictactoe.controller.parser.RegexMoveParser;
import it.unibo.tictactoe.controller.prompt.JsonMovePrompt;
import it.unibo.tictactoe.model.BoardImpl;
import it.unibo.tictactoe.model.Player;
import it.unibo.utils.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIPlayerTest {

    private static final int TIMEOUT_MILLIS = 5000;

    @Mock private ChatModel mockModel;

    @Test
    void shouldReturnParsedMoveFromJsonResponse() throws Exception {
        when(mockModel.chat(anyString())).thenReturn("{\"row\":1,\"col\":2}");
        final AIPlayer player = new AIPlayer(mockModel, Player.O);
        final Pair<Integer, Integer> move = player.getNextMove(new BoardImpl())
            .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(Pair.of(1, 2), move);
        verify(mockModel, atLeastOnce()).chat(anyString());
    }

    @Test
    void shouldHandleJsonInsideMarkdownFencing() throws Exception {
        when(mockModel.chat(anyString()))
            .thenReturn("```json\n{\"row\":0,\"col\":1}\n```");
        final AIPlayer player = new AIPlayer(mockModel, Player.O);
        final Pair<Integer, Integer> move = player.getNextMove(new BoardImpl())
            .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(Pair.of(0, 1), move);
    }

    @Test
    void shouldFallbackOnUnparseableResponse() throws Exception {
        when(mockModel.chat(anyString())).thenReturn("I have no idea");
        final AIPlayer player = new AIPlayer(mockModel, Player.O);
        final var board = new BoardImpl();
        board.setCell(0, 0, Player.X);
        final Pair<Integer, Integer> move = player.getNextMove(board)
            .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(Pair.of(0, 1), move);
    }

    @Test
    void shouldFallbackWhenLlmSuggestsOccupiedCell() throws Exception {
        when(mockModel.chat(anyString())).thenReturn("{\"row\":0,\"col\":0}");
        final AIPlayer player = new AIPlayer(mockModel, Player.O);
        var board = new BoardImpl();
        board.setCell(0, 0, Player.X);
        Pair<Integer, Integer> move = player.getNextMove(board)
            .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(Pair.of(0, 1), move);
    }

    @Test
    void shouldFallbackWhenLlmSuggestsOutOfBounds() throws Exception {
        when(mockModel.chat(anyString())).thenReturn("{\"row\":9,\"col\":9}");
        final Pair<Integer, Integer> move = new AIPlayer(mockModel, Player.O)
            .getNextMove(new BoardImpl())
            .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(Pair.of(0, 0), move);
    }

    @Test
    void shouldCallModelMultipleTimesBeforeFallback() throws Exception {
        when(mockModel.chat(anyString()))
            .thenReturn("hmm").thenReturn("nah").thenReturn("dunno")
            .thenReturn("??").thenReturn("hmm again");
        Pair<Integer, Integer> move = new AIPlayer(mockModel, Player.O)
            .getNextMove(new BoardImpl())
            .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(Pair.of(0, 0), move);
        verify(mockModel, atLeastOnce()).chat(anyString());
    }

    @Test
    void shouldWorkWithRegexParserViaConstructor() throws Exception {
        when(mockModel.chat(anyString())).thenReturn("My move is 2,1");
        AIPlayer player = new AIPlayer(
            mockModel, new JsonMovePrompt(Player.O), new RegexMoveParser()
        );
        Pair<Integer, Integer> move = player.getNextMove(new BoardImpl())
            .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(Pair.of(2, 1), move);
    }

}
