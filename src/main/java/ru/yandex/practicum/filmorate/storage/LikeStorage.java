package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.LikeCounter;
import ru.yandex.practicum.filmorate.storage.mappers.LikeCounterRowMapper;

import java.util.Comparator;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeStorage {
    private final JdbcTemplate jdbc;
    private final LikeCounterRowMapper likeCountMapper;

    private static final String ADD_LIKE_QUERY = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_ALL_LIKES_QUERY = "SELECT film_id, COUNT(*) as like_count from likes group by film_id LIMIT ?";

    public void addLike(int filmId, int userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        jdbc.update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    public List<LikeCounter> getTopFilms(int maxLimit) {
        List<LikeCounter> likeStats = jdbc.query(FIND_ALL_LIKES_QUERY, likeCountMapper, maxLimit);
        return likeStats.stream()
                .sorted(Comparator.comparingInt(LikeCounter::getLikeCount).reversed())
                .toList();
    }
}
