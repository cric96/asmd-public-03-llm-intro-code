package it.unibo.tictactoe.controller.parser;

import com.google.gson.Gson;
import it.unibo.utils.Pair;

import java.util.Optional;

public final class GsonMoveParser implements MoveParser {

    private final Gson gson = new Gson();

    @Override
    public Optional<Pair<Integer, Integer>> parse(String response) {
        try {
            String json = extractJson(response);
            MoveResponse move = gson.fromJson(json, MoveResponse.class);
            return Optional.of(Pair.of(move.row(), move.col()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) return text.substring(start, end + 1);
        throw new IllegalArgumentException("No JSON object found");
    }
}
