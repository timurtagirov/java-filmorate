package ru.yandex.practicum.filmorate.model;

import java.util.Optional;

public class RatingConverter {
    public static Optional<Integer> convertToId(String rating) {
        switch (rating) {
            case null:
                return Optional.empty();
            case "G":
                return Optional.of(1);
            case "PG":
                return Optional.of(2);
            case "PG13":
                return Optional.of(3);
            case "R":
                return Optional.of(4);
            default:
                return Optional.of(5);
        }
    }
}
