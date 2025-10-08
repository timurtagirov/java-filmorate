package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmToGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmToGenreRowMapper implements RowMapper<FilmToGenre> {
    @Override
    public FilmToGenre mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        FilmToGenre filmToGenre = new FilmToGenre();
        filmToGenre.setFilmId(resultSet.getInt("film_id"));
        filmToGenre.setGenre(new Genre(resultSet.getInt("genre_id"), resultSet.getString("genre")));
        return filmToGenre;
    }
}
