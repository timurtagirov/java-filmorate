package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class LikeCounter {
    private int filmId;
    private int likeCount;
}
