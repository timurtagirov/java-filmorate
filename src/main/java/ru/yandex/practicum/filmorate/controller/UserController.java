package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        log.debug("Getting the list of users");
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.debug("Creating a user a film...");
        try {
            if (user.getEmail() == null || user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
                throw new ValidationException("Incorrect email");
            }
            if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                throw new ValidationException("Incorrect login");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Incorrect birthday");
            }
            log.trace("The data passed all checks. Adding the user to the library...");
            user.setId(getNextId());
            users.put(user.getId(), user);
            log.debug("The user ", user.getName(), " has been successfully created");
        } catch (ValidationException e) {
            log.error("Error! ", e);
            throw e;
        }
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.debug("Updating a user...");
        try {
            if (user.getEmail() == null || user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
                throw new ValidationException("Incorrect email");
            }
            if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                throw new ValidationException("Incorrect login");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Incorrect birthday");
            }
            if (!users.containsKey(user.getId())) {
                throw new NotFoundException("A user with such ID doesn't exist");
            }
            log.trace("The data passed all checks. Updating the user...");
            User oldUser = users.get(user.getId());
            oldUser.setEmail(user.getEmail());
            oldUser.setLogin(user.getLogin());
            oldUser.setName(user.getName());
            oldUser.setBirthday(user.getBirthday());
            log.debug("The user ", oldUser.getName(), "  has been successfully updated");
            return oldUser;
        } catch (RuntimeException e) {
            log.error("Error! ", e);
            throw e;
        }

    }

    private Integer getNextId() {
        Integer currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
