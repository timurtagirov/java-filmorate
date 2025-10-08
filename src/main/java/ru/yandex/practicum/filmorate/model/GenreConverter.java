package ru.yandex.practicum.filmorate.model;

public class GenreConverter {
    public static int convertToId(String genre) {
        switch (genre) {
            case "Comedy":
                return 1;
            case "Drama":
                return 2;
            case "Action":
                return 3;
            default:
                return 4;
        }
    }
}
