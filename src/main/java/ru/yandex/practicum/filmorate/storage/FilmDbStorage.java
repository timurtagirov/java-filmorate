package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmMapper;
    private final GenreMapper genreMapper;
    final LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);
    private static final String FIND_ALL_FILMS_QUERY = "SELECT t1.*, t2.name AS rating_name FROM films t1 " +
            "LEFT JOIN ratings t2 ON t1.rating_id = t2.id";
    private static final String FIND_ALL_GENRES_QUERY = "SELECT t1.film_id, t1.genre_id, t2.name AS genre " +
            "FROM films_to_genres t1 LEFT JOIN genres t2 ON t1.genre_id = t2.id";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT t1.*, t2.name AS rating_name FROM films t1 " +
            "LEFT JOIN ratings t2 ON t1.rating_id = t2.id " +
            "WHERE t1.id = ?";
    private static final String FIND_GENRE_BY_FILM_ID_QUERY = "SELECT DISTINCT t1.genre_id, t2.name AS genre FROM films_to_genres t1 " +
            "JOIN genres t2 ON t1.genre_id = t2.id " +
            "WHERE t1.film_id = ?";
    private static final String DELETE_FILM_FROM_GENRES_QUERY = "DELETE FROM films_to_genres WHERE film_id = ?";
    private static final String DELETE_FILM_FROM_LIKES_QUERY = "DELETE FROM likes WHERE film_id = ?";
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, " +
            "release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO films(name, description, release_date, duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_GENRE_QUERY = "INSERT INTO films_to_genres(film_id, genre_id) " +
            "VALUES (?, ?)";


    @Override
    public Collection<Film> getAllFilms() {
        // Получаем список фильмов с рейтингами
        List<Film> films = jdbc.query(FIND_ALL_FILMS_QUERY, filmMapper);

        // получаем список всех пар фильм-жанр
        Map<Integer, List<Genre>> filmGenres = new HashMap<>();
        jdbc.query(FIND_ALL_GENRES_QUERY, rs -> {
            int filmId = rs.getInt("film_id");
            filmGenres.computeIfAbsent(filmId, key -> new ArrayList<>())
                    .add(new Genre(rs.getInt("genre_id"), rs.getString("genre")));
        });

        // проходимся по всем фильмам, и подставляем туда жанры
        for (Film film : films) {
            if (filmGenres.containsKey(film.getId())) {
                List<Genre> genres = filmGenres.get(film.getId());
                film.getGenres().addAll(genres);
            }
        }
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        checkFilm(film);
        // Добавляем в фильм
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_FILM_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());

            int ratingId = film.getMpa().getId();
            ps.setObject(5, ratingId);

            return ps;
        }, keyHolder);

        // Добавляем сгенерированный id фильма в film, он нужен нам, чтобы указать в жанрах
        Number key = Objects.requireNonNull(keyHolder.getKey(), "No key generated for films");
        int filmId = key.intValue();
        film.setId(filmId);

        // Добавляем в жанры
        if (!film.getGenres().isEmpty()) {
            //List<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
            List<Integer> genreIds = film.getGenres().stream().map(Genre::getId).distinct().toList();
            jdbc.batchUpdate(
                    INSERT_GENRE_QUERY,
                    genreIds,
                    film.getGenres().size(),
                    (ps, genreId) -> {
                        ps.setInt(1, filmId);
                        ps.setInt(2, genreId);
                    }
            );
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int filmId = film.getId();
        if (!film.getGenres().isEmpty()) {
            List<Integer> genreIds = film.getGenres()
                    .stream().map(Genre::getId)
                    .distinct()             // важно, чтобы не ловить уникальные конфликты
                    .toList();
            jdbc.update(DELETE_FILM_FROM_GENRES_QUERY, filmId);
            jdbc.batchUpdate(
                    INSERT_GENRE_QUERY,
                    genreIds,
                    genreIds.size(),
                    (ps, genreId) -> {
                        ps.setInt(1, filmId);
                        ps.setInt(2, genreId);
                    }
            );
        }

        int rowsUpdated = jdbc.update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
        return film;
    }

    @Override
    public Film removeFilm(int id) {
        Film film = getFilm(id);
        jdbc.update(DELETE_FILM_FROM_GENRES_QUERY, id); // удаляем из пар фильм-жанр
        jdbc.update(DELETE_FILM_FROM_LIKES_QUERY, id);  // удаляем из таблицы с лайками
        int rowsDeleted = jdbc.update(DELETE_FILM_QUERY, id);  // удаляем из таблицы с фильмами
        if (rowsDeleted == 0) {
            throw new NotFoundException("A film with such ID doesn't exist");
        }
        return film;
    }

    @Override
    public Film getFilm(Integer id) {
        Film film = new Film();
        try {
            film = jdbc.queryForObject(FIND_FILM_BY_ID_QUERY, filmMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("A film with such ID doesn't exist: " + id);
        }
        List<Genre> genres = jdbc.query(FIND_GENRE_BY_FILM_ID_QUERY, genreMapper, id);
        film.setGenres(new ArrayList<>(genres));
        return film;
    }

    private void checkFilm(Film film) {
        if (film.getReleaseDate().isBefore(earliestReleaseDate)) {
            throw new ValidationException("Release date can't be earlier than Dec, 28, 1895");
        }
        if (film.getMpa().getId() > 5) {
            throw new NotFoundException("There is no such MPA");
        }
        if (film.getGenres().isEmpty()) {
            return;
        }
        for (Genre genre : film.getGenres()) {
            if (genre.getId() > 6) {
                throw new NotFoundException("There is no such genre");
            }
        }
    }
}
