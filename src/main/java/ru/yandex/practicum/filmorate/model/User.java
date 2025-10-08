package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    Integer id;

    @NotNull(message = "Please provide an email")
    @Email(message = "Incorrect email")
    String email;

    @NotBlank(message = "Incorrect login")
    String login;

    String name;

    @NotNull(message = "Please provide birthday")
    @Past(message = "Incorrect birthday")
    LocalDate birthday;

    Set<Integer> friends = new HashSet<>();
}
