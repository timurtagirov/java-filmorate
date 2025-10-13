package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Film.
 */
@Data
public class Film {
    private Integer id;

    @NotBlank(message = "Please provide a name of the film")
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull(message = "Please provide release date")
    private LocalDate releaseDate;

    @NotNull(message = "Please provide duration of a movie")
    @Positive(message = "Duration of a movie can't be negative")
    private Integer duration;

    private List<Genre> genres = new ArrayList<>();
    private Mpa mpa;
}
