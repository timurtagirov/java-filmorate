package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    HashMap<Integer, Film> films = new HashMap<>();
    final LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);

    public Collection<Film> getAllFilms() {
        log.debug("Getting the list of films");
        return films.values();
    }

    public Film createFilm(Film film) {
        log.debug("Creating a film...");
        checkFilm(film);
        log.trace("The film passed all checks. Adding it to the library...");
        film.setId(IdGenerator.getNextId(films));
        films.put(film.getId(), film);
        log.debug("The film ", film.getName(), " has been successfully created");
        return film;
    }

    public Film updateFilm(Film film) {
        log.debug("Updating a film...");
        checkFilm(film);
        if (!films.containsKey(film.getId())) {
            log.error("Error! A film with such ID doesn't exist");
            throw new NotFoundException("A film with such ID doesn't exist");
        }
        log.trace("The film passed all checks. Updating it...");
        Film oldFilm = films.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());
        log.debug("The film ", oldFilm.getName(), "  has been successfully updated");
        return oldFilm;
    }

    public Film removeFilm(Film film) {
        log.debug("Removing a film...");
        if (!films.containsKey(film.getId())) {
            log.error("Error! A film with such ID doesn't exist");
            throw new NotFoundException("A film with such ID doesn't exist");
        }
        log.trace("The film passed all checks. Updating it...");
        films.remove(film.getId());
        log.debug("The film ", film.getName(), "  has been successfully removed");
        return film;
    }

    public Film getFilm(Integer id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("A film with such ID doesn't exist: " + id);
        }
        return films.get(id);
    }

    private void checkFilm(Film film) {
        if (film.getReleaseDate().isBefore(earliestReleaseDate)) {
            log.error("Error! Release date can't be earlier than Dec, 28, 1895");
            throw new ValidationException("Release date can't be earlier than Dec, 28, 1895");
        }
    }
}
