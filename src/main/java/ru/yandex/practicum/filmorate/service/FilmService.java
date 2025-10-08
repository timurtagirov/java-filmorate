package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.LikeCounter;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;


    @Autowired
    public FilmService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage,
                       LikeStorage likeStorage,
                       MpaStorage mpaStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
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
        likeStorage.addLike(filmId, userId);
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
        likeStorage.removeLike(filmId, userId);
        log.debug("The like is successfully removed");
    }

    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> allFilms = getAllFilms();
        Map<Integer, Film> filmMap = allFilms.stream()
                .collect(Collectors.toMap(Film::getId, f -> f));
        List<LikeCounter> sortedLikesStats = likeStorage.getTopFilms(count);
        List<Integer> filmIds = sortedLikesStats.stream()
                .map(LikeCounter::getFilmId).toList();
        List<Film> sortedFilms = new ArrayList<>();
        sortedFilms = filmIds.stream()
                .map(filmMap::get)
                .filter(Objects::nonNull)
                .toList();
        return sortedFilms;
    }
}
