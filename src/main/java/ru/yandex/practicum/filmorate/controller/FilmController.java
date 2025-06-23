package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    HashMap<Integer, Film> films = new HashMap<>();
    final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.debug("Getting the list of films");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.debug("Creating a film...");
        try {
            if (film.getName() == null || film.getName().isBlank()) {
                throw new ValidationException("The name of the film shouldn't be empty");
            }
            if (film.getDescription() != null && film.getDescription().length() > 200) {
                throw new ValidationException("Description shouldn't exceed 200 symbols");
            }
            if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
                throw new ValidationException("Release date can't be earlier than Dec, 28, 1895");
            }
            if (film.getDuration() == null || film.getDuration() < 0) {
                throw new ValidationException("Duration of a movie can't be negative");
            }
            log.trace("The film passed all checks. Adding it to the library...");
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.debug("The film ", film.getName(), " has been successfully created");
        } catch (ValidationException e) {
            log.error("Error! ", e);
            throw e;
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.debug("Updating a film...");
        try {
            if (film.getName() == null || film.getName().isBlank()) {
                throw new ValidationException("The name of the film shouldn't be empty");
            }
            if (film.getDescription() != null && film.getDescription().length() > 200) {
                throw new ValidationException("Description shouldn't exceed 200 symbols");
            }
            if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
                throw new ValidationException("Release date can't be earlier than Dec, 28, 1895");
            }
            if (film.getDuration() == null || film.getDuration() < 0) {
                throw new ValidationException("Duration of a movie can't be negative");
            }
            if (!films.containsKey(film.getId())) {
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
        } catch (RuntimeException e) {
            log.error("Error! ", e);
            throw e;
        }
    }


    private Integer getNextId() {
        Integer currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
