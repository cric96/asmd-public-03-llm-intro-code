package it.unibo.tictactoe.controller.parser;

import com.google.gson.Gson;
import it.unibo.utils.Pair;

import java.util.Optional;

public final class GsonMoveParser implements MoveParser {

    private final Gson gson = new Gson();

    @Override
    public Optional<Pair<Integer, Integer>> parse(String response) {
        try {
            MoveResponse move = gson.fromJson(response, MoveResponse.class);
            return Optional.of(Pair.of(move.row(), move.col()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
