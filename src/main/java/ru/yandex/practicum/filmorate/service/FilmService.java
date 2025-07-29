package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

@Slf4j
@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getAllFilms() {
        log.debug("Getting the list of films");
        return filmStorage.getAllFilms();
    }

    public Film getFilm(Integer id) {
        return filmStorage.getFilm(id);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLike(Integer filmId, Integer userId) {
        log.debug("Adding a like");
        boolean userExists = userStorage.getAllUsers().stream().map(User::getId).anyMatch(id -> Objects.equals(id, userId));
        if (!userExists) {
            log.debug("There is no such user. Throwing an exception");
            throw new NotFoundException("A user with such ID doesn't exist: " + userId);
        }
        log.trace("The user is found. Proceed to adding like");
        // Проверку на наличие фильма не стал писать, потому что getFilm() в этом случае и так выбросит ошибку
        filmStorage.getFilm(filmId).getLikes().add(userId);
        log.debug("The like is successfully added");
    }

    public void removeLike(Integer filmId, Integer userId) {
        log.debug("Removing a like");
        boolean userExists = userStorage.getAllUsers().stream().map(User::getId).anyMatch(id -> Objects.equals(id, userId));
        if (!userExists) {
            log.debug("There is no such user. Throwing an exception");
            throw new NotFoundException("A user with such ID doesn't exist: " + userId);
        }
        log.trace("The user is found. Proceed to adding like");
        // Проверку на наличие фильма не стал писать, потому что getFilm() в этом случае и так выбросит ошибку
        filmStorage.getFilm(filmId).getLikes().remove(userId);
        log.debug("The like is successfully removed");
    }

    public Collection<Film> getPopularFilms(int count) {
        log.debug("Getting top of the most popular films");
        return filmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }
}
