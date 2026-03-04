package it.unibo.tictactoe.controller;

import dev.langchain4j.model.chat.ChatModel;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIPlayerTest {

    private static final int TIMEOUT_MILLIS = 5000;

    @Mock
    private ChatModel mockModel;

    @Test
    void shouldReturnParsedMoveFromLlm() throws Exception {
        when(mockModel.chat(anyString())).thenReturn("1,2");
        AIPlayer player = new AIPlayer(mockModel, Player.O);
        var board = new BoardImpl();
        Pair<Integer, Integer> move = player.getNextMove(board)
                .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(Pair.of(1, 2), move);
        verify(mockModel, atLeastOnce()).chat(anyString());
    }

    @Test
    void shouldHandleResponseWithSurroundingText() throws Exception {
        when(mockModel.chat(anyString())).thenReturn("I choose position 0,1 for my move");
        AIPlayer player = new AIPlayer(mockModel, Player.O);
        var board = new BoardImpl();
        Pair<Integer, Integer> move = player.getNextMove(board)
            .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(Pair.of(0, 1), move);
    }

    @Test
    void shouldFallbackToFirstEmptyCellOnUnparseableResponse() throws Exception {
        when(mockModel.chat(anyString())).thenReturn("I have no idea what to do");
        AIPlayer player = new AIPlayer(mockModel, Player.O);
        var board = new BoardImpl();
        board.setCell(0, 0, Player.X);
        Pair<Integer, Integer> move = player.getNextMove(board)
            .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(Pair.of(0, 1), move);
    }

    @Test
    void shouldFallbackWhenLlmSuggestsOccupiedCell() throws Exception {
        when(mockModel.chat(anyString())).thenReturn("0,0");
        AIPlayer player = new AIPlayer(mockModel, Player.O);
        var board = new BoardImpl();
        board.setCell(0, 0, Player.X);
        Pair<Integer, Integer> move = player.getNextMove(board)
            .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(Pair.of(0, 1), move);
    }

    @Test
    void shouldFallbackWhenLlmSuggestsOutOfBoundsMove() throws Exception {
        when(mockModel.chat(anyString())).thenReturn("9,9");
        AIPlayer player = new AIPlayer(mockModel, Player.O);
        var board = new BoardImpl();
        Pair<Integer, Integer> move = player.getNextMove(board)
            .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(Pair.of(0, 0), move);
    }

    @Test
    void shouldCallModelMultipleTimesBeforeFallback() throws Exception {
        when(mockModel.chat(anyString()))
            .thenReturn("hmm")
            .thenReturn("nah")
            .thenReturn("dunno")
            .thenReturn("??")
            .thenReturn("hmm again");
        AIPlayer player = new AIPlayer(mockModel, Player.O);
        var board = new BoardImpl();
        Pair<Integer, Integer> move = player.getNextMove(board)
            .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(Pair.of(0, 0), move);
        verify(mockModel, atLeastOnce()).chat(anyString());
    }
    
    @Nested
    class MoveParserTest {
        private AIPlayer.MoveParser parser;

        @BeforeEach
        void setUp() {
            parser = new AIPlayer.MoveParser();
        }

        @Test
        void shouldParseCommaSeparatedDigits() {
            assertEquals(Optional.of(Pair.of(1, 2)), parser.parse("1,2"));
        }

        @Test
        void shouldParseWithSpaceAfterComma() {
            assertEquals(Optional.of(Pair.of(0, 1)), parser.parse("0, 1"));
        }

        @Test
        void shouldParseSpaceSeparatedDigits() {
            assertEquals(Optional.of(Pair.of(2, 0)), parser.parse("2 0"));
        }

        @Test
        void shouldParseDigitsEmbeddedInSentence() {
            assertEquals(Optional.of(Pair.of(2, 0)), parser.parse("My move is 2,0."));
        }

        @Test
        void shouldReturnEmptyForNoNumbers() {
            assertEquals(Optional.empty(), parser.parse("no numbers here"));
        }

        @Test
        void shouldReturnEmptyForSingleNumber() {
            assertEquals(Optional.empty(), parser.parse("just 5"));
        }
    }

    @Nested
    class PromptBuilderTest {

        private AIPlayer.PromptBuilder promptBuilder;

        @BeforeEach
        void setUp() {
            promptBuilder = new AIPlayer.PromptBuilder(Player.O);
        }

        @Test
        void shouldContainPlayerSymbol() {
            var board = new BoardImpl();
            board.setCell(0, 0, Player.X);
            String prompt = promptBuilder.build(board);
            assertTrue(prompt.contains("O"), "Prompt should mention the AI player symbol");
            assertTrue(prompt.contains("X"), "Prompt should show existing X on board");
        }

        @Test
        void shouldListAvailablePositions() {
            var board = new BoardImpl();
            board.setCell(0, 0, Player.X);
            board.setCell(1, 1, Player.O);
            String prompt = promptBuilder.build(board);
            assertTrue(prompt.contains("(0,1)"));
            assertTrue(prompt.contains("(2,2)"));
            assertFalse(prompt.contains("Available positions: (0,0)"),
                    "Occupied cell (0,0) should not be listed");
        }

        @Test
        void shouldMentionBoardSize() {
            var board = new BoardImpl();
            String prompt = promptBuilder.build(board);

            assertTrue(prompt.contains("3x3"), "Prompt should state board dimensions");
        }
    }

    @Nested
    class BoardFormatterTest {
        @Test
        void shouldFormatEmptyBoard() {
            var board = new BoardImpl();
            String formatted = AIPlayer.BoardFormatter.format(board);
            long dotCount = formatted.chars().filter(c -> c == '.').count();
            assertEquals(9, dotCount, "Empty board should have 9 dot symbols");
        }

        @Test
        void shouldShowPlayerSymbolsInFormattedBoard() {
            var board = new BoardImpl();
            board.setCell(0, 0, Player.X);
            board.setCell(1, 1, Player.O);
            String formatted = AIPlayer.BoardFormatter.format(board);
            assertTrue(formatted.contains("X"));
            assertTrue(formatted.contains("O"));
        }

        @Test
        void emptyCellDescriptionsShouldListAllCellsForEmptyBoard() {
            var board = new BoardImpl();
            String descriptions = AIPlayer.BoardFormatter.emptyCellDescriptions(board);
            // Should contain all 9 positions
            assertTrue(descriptions.contains("(0,0)"));
            assertTrue(descriptions.contains("(2,2)"));
        }

        @Test
        void emptyCellDescriptionsShouldExcludeOccupiedCells() {
            var board = new BoardImpl();
            board.setCell(0, 0, Player.X);
            board.setCell(2, 2, Player.O);
            String descriptions = AIPlayer.BoardFormatter.emptyCellDescriptions(board);
            assertFalse(descriptions.contains("(0,0)"));
            assertFalse(descriptions.contains("(2,2)"));
            assertTrue(descriptions.contains("(1,1)"));
        }
    }
}
