package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    Integer id;

    @NotBlank(message = "Please provide a name of the film")
    String name;

    @Size(max = 200)
    String description;

    @NotNull(message = "Please provide release date")
    LocalDate releaseDate;

    @NotNull(message = "Please provide duration of a movie")
    @Positive(message = "Duration of a movie can't be negative")
    Integer duration;

    Set<Integer> likes = new HashSet<>();
}
