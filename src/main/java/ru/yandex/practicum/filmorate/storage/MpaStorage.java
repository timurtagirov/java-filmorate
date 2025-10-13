package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class MpaStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mpaMapper;

    private static final String FIND_MPA_BY_ID_QUERY = "SELECT id, name from ratings WHERE id = ?";
    private static final String FIND_ALL_MPAS_QUERY = "SELECT id, name from ratings";

    public Mpa getMpaById(int id) {
        try {
            return jdbc.queryForObject(FIND_MPA_BY_ID_QUERY, mpaMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("A film with such ID doesn't exist: " + id);
        }
    }

    public Collection<Mpa> getAllMpas() {
        // Получаем список фильмов с рейтингами
        return jdbc.query(FIND_ALL_MPAS_QUERY, mpaMapper);
    }
}
