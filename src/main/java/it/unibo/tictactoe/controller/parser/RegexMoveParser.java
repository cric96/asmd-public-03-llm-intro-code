package it.unibo.tictactoe.controller.parser;

import it.unibo.utils.Pair;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexMoveParser implements MoveParser {

    private static final Pattern MOVE_PATTERN =
        Pattern.compile("(\\d)\\s*[,\\s]\\s*(\\d)");

    @Override
    public Optional<Pair<Integer, Integer>> parse(String response) {
        Matcher matcher = MOVE_PATTERN.matcher(response);
        if (matcher.find()) {
            int row = Integer.parseInt(matcher.group(1));
            int col = Integer.parseInt(matcher.group(2));
            return Optional.of(Pair.of(row, col));
        }
        return Optional.empty();
    }
}
