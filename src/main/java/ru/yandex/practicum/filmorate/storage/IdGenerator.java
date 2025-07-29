package ru.yandex.practicum.filmorate.storage;

import java.util.HashMap;

class IdGenerator {
    static <T> int getNextId(HashMap<Integer, T> map) {
        int currentMaxId = map.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
