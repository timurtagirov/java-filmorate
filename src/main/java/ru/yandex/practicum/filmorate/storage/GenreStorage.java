package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreMapper;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class GenreStorage {
    private final JdbcTemplate jdbc;
    private final GenreMapper mapper;

    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT id AS genre_id, name AS genre from genres WHERE id = ?";
    private static final String FIND_ALL_GENRES_QUERY = "SELECT id AS genre_id, name AS genre from genres";

    public Genre getGenreById(int id) {
        try {
            return jdbc.queryForObject(FIND_GENRE_BY_ID_QUERY, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Genre with such ID doesn't exist: " + id);
        }
    }

    public Collection<Genre> getAllGenres() {
        // Получаем список фильмов с рейтингами
        return jdbc.query(FIND_ALL_GENRES_QUERY, mapper);
    }
}
