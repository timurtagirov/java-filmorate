package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    @Test
    public void testPostFilm() {
        Film film = new Film();
        film.setName("Lord of the rings");
        film.setReleaseDate(LocalDate.of(2001, 7, 15));
        film.setDuration(120);

        // A film should be posted
        FilmController filmController = new FilmController();
        filmController.createFilm(film);
        assertEquals(1, filmController.getAllFilms().size());

        // empty film shouldn't be posted
        Film emptyFilm = new Film();
        assertThrows(NullPointerException.class, () -> {
            filmController.createFilm(emptyFilm);
        });

        // Film with release date in 1894 shouldn't be posted
        Film wrongReleaseDateFilm = new Film();
        wrongReleaseDateFilm.setName("Pirates of the caribbean");
        wrongReleaseDateFilm.setReleaseDate(LocalDate.of(1895, 12, 28).minusDays(1));
        wrongReleaseDateFilm.setDuration(120);
        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(wrongReleaseDateFilm);
        });
    }

    @Test
    public void testPutFilm() {
        Film film = new Film();
        film.setName("Lord of the rings");
        film.setReleaseDate(LocalDate.of(2001, 7, 15));
        film.setDuration(120);

        // A film should be posted
        FilmController filmController = new FilmController();
        filmController.createFilm(film);
        assertEquals(1, filmController.getAllFilms().size());

        // A film should be updated
        film.setDescription("Description");
        film.setId(1);
        filmController.updateFilm(film);
        assertEquals("Description", filmController.getAllFilms().stream().toList().getFirst().getDescription());

        // A film shouldn't be updated if the input is empty film
        Film emptyFilm = new Film();
        emptyFilm.setId(1);
        assertThrows(NullPointerException.class, () -> {
            filmController.updateFilm(emptyFilm);
        });

        // Film with release date in 1894 shouldn't be updated
        Film wrongReleaseDateFilm = new Film();
        wrongReleaseDateFilm.setName("Pirates of the caribbean");
        wrongReleaseDateFilm.setReleaseDate(LocalDate.of(1895, 12, 28).minusDays(1));
        wrongReleaseDateFilm.setDuration(120);
        wrongReleaseDateFilm.setId(1);
        assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(wrongReleaseDateFilm);
        });
    }
}
