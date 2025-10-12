package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class,
        FilmDbStorage.class,
        UserRowMapper.class,
        FilmRowMapper.class,
        GenreMapper.class})
class FilmorateApplicationTest {
    private final JdbcTemplate jdbc;
    private final UserRowMapper userMapper;
    private final FilmRowMapper filmMapper;
    private final GenreMapper genreMapper;
    private UserDbStorage userStorage;
    private FilmDbStorage filmStorage;
    private static final String TRUNCATE_FILM_QUERY = "DELETE FROM films";
    private static final String TRUNCATE_RESTART_ID_FILM_QUERY = "ALTER TABLE films ALTER COLUMN id RESTART WITH 1";

    @BeforeEach
    public void setUp() {
        userStorage = new UserDbStorage(jdbc, userMapper);
        filmStorage = new FilmDbStorage(jdbc, filmMapper, genreMapper);
    }

    @Test
    public void testAddUserAndGetAllUsers() {
        User user = new User();
        user.setEmail("someemail@gmail.com");
        user.setLogin("JohnSmith");
        user.setName("John");
        user.setBirthday(LocalDate.of(2001, 7, 15));
        userStorage.createUser(user);

        User secondUser = new User();
        secondUser.setEmail("secondemail@gmail.com");
        secondUser.setLogin("BobWilliams");
        secondUser.setName("Bob");
        secondUser.setBirthday(LocalDate.of(2000, 5, 12));
        userStorage.createUser(secondUser);

        List<User> users = new ArrayList<>(userStorage.getAllUsers());
        assertTrue(users.size() == 2 &&
                users.getFirst().getName().equals("John") &&
                users.getLast().getName().equals("Bob"));
    }

    @Test
    public void testUpdateAndGetUser() {
        User user = new User();
        user.setEmail("someemail@gmail.com");
        user.setLogin("JohnSmith");
        user.setName("John");
        user.setBirthday(LocalDate.of(2001, 7, 15));
        userStorage.createUser(user);

        User newUser = new User();
        newUser.setLogin("JohnSmith");
        newUser.setName("John");
        newUser.setBirthday(LocalDate.of(2001, 7, 15));
        newUser.setId(1);
        newUser.setEmail("someemail@yahoo.com");
        userStorage.updateUser(newUser);

        User userFromSql = userStorage.getUser(1);
        assertEquals("someemail@yahoo.com", userFromSql.getEmail());
    }

    @Test
    public void testRemoveUser() {
        User user = new User();
        user.setEmail("someemail@gmail.com");
        user.setLogin("JohnSmith");
        user.setName("John");
        user.setBirthday(LocalDate.of(2001, 7, 15));
        userStorage.createUser(user);

        User secondUser = new User();
        secondUser.setEmail("secondemail@gmail.com");
        secondUser.setLogin("BobWilliams");
        secondUser.setName("Bob");
        secondUser.setBirthday(LocalDate.of(2000, 5, 12));
        userStorage.createUser(secondUser);

        userStorage.removeUser(user.getId());
        List<User> users = new ArrayList<>(userStorage.getAllUsers());
        assertTrue(users.size() == 1 &&
                users.getFirst().getName().equals("Bob"));
    }

    @Test
    public void testAddFilmAndGetAllFilms() {
        Film film = new Film();
        film.setName("Lord of the rings");
        film.setReleaseDate(LocalDate.of(2001, 7, 15));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "PG"));
        filmStorage.createFilm(film);

        Film secondFilm = new Film();
        secondFilm.setName("Pirates of the caribbean");
        secondFilm.setReleaseDate(LocalDate.of(2003, 7, 15));
        secondFilm.setDuration(130);
        secondFilm.setMpa(new Mpa(2, "PG13"));
        filmStorage.createFilm(secondFilm);

        List<Film> films = new ArrayList<>(filmStorage.getAllFilms());
        assertTrue(films.size() == 2 &&
                films.getFirst().getName().equals("Lord of the rings") &&
                films.getLast().getName().equals("Pirates of the caribbean"));
    }

    @Test
    public void testUpdateAndGeFilm() {
        jdbc.update(TRUNCATE_FILM_QUERY);
        jdbc.update(TRUNCATE_RESTART_ID_FILM_QUERY);
        Film film = new Film();
        film.setName("Lord of the rings");
        film.setReleaseDate(LocalDate.of(2001, 7, 15));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "PG"));
        filmStorage.createFilm(film);

        List<Film> checkFilms = new ArrayList<>(filmStorage.getAllFilms());
        for (Film someFilm : checkFilms) {
            System.out.println(someFilm.getId());
        }

        film.setDescription("Description");
        film.setId(1);
        filmStorage.updateFilm(film);

        Film filmFromSql = filmStorage.getFilm(film.getId());
        assertEquals("Description", filmFromSql.getDescription());
    }

    @Test
    public void testRemoveFilm() {
        Film film = new Film();
        film.setName("Lord of the rings");
        film.setReleaseDate(LocalDate.of(2001, 7, 15));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "PG"));
        filmStorage.createFilm(film);

        Film secondFilm = new Film();
        secondFilm.setName("Pirates of the caribbean");
        secondFilm.setReleaseDate(LocalDate.of(2003, 7, 15));
        secondFilm.setDuration(130);
        secondFilm.setMpa(new Mpa(2, "PG13"));
        filmStorage.createFilm(secondFilm);

        filmStorage.removeFilm(film.getId());
        List<Film> films = new ArrayList<>(filmStorage.getAllFilms());
        assertTrue(films.size() == 1 &&
                films.getFirst().getName().equals("Pirates of the caribbean"));
    }
}
