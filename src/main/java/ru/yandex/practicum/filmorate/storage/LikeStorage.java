package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class LikeStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmMapper;

    private static final String ADD_LIKE_QUERY = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_TOP_FILMS_QUERY =
            "SELECT t1.id, t1.name, t1.description, t1.release_date, t1.duration, t1.rating_id, t2.name AS rating_name " +
                    "FROM films t1 " +
                    "LEFT JOIN ratings t2 ON t1.rating_id = t2.id " +
                    "JOIN (SELECT film_id, COUNT(*) as like_count from likes group by film_id ORDER BY COUNT(*) DESC LIMIT ?) t3 ON t1.id = t3.film_id " +
                    "ORDER BY like_count DESC";
    private static final String FIND_TOP_FILMS_GENRES_QUERY = "SELECT t1.film_id, t1.genre_id, t2.name AS genre FROM films_to_genres t1 " +
            "LEFT JOIN genres t2 ON t1.genre_id = t2.id " +
            "JOIN (SELECT film_id, COUNT(*) as like_count from likes group by film_id ORDER BY COUNT(*) DESC LIMIT ?) t3 ON t1.film_id = t3.film_id";

    public void addLike(int filmId, int userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        jdbc.update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    public List<Film> getTopFilms(int maxLimit) {
        List<Film> topFilms = jdbc.query(FIND_TOP_FILMS_QUERY, filmMapper, maxLimit);

        Map<Integer, List<Genre>> filmGenres = new HashMap<>();
        jdbc.query(FIND_TOP_FILMS_GENRES_QUERY, ps -> ps.setInt(1, maxLimit), rs -> {
            int filmId = rs.getInt("film_id");
            filmGenres.computeIfAbsent(filmId, key -> new ArrayList<>())
                    .add(new Genre(rs.getInt("genre_id"), rs.getString("genre")));
        });

        for (Film film : topFilms) {
            if (filmGenres.containsKey(film.getId())) {
                List<Genre> genres = filmGenres.get(film.getId());
                film.getGenres().addAll(genres);
            }
        }
        return topFilms;
    }
}
