package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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
    public User createUser(@Valid @RequestBody User user) {
        log.debug("Creating a user a film...");
        checkUser(user);
        log.trace("The data passed all checks. Adding the user to the library...");
        user.setId(IdGenerator.getNextId(users));
        users.put(user.getId(), user);
        log.debug("The user ", user.getName(), " has been successfully created");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Updating a user...");
        checkUser(user);
        if (!users.containsKey(user.getId())) {
            log.error("Error! A user with such ID doesn't exist");
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
    }

    private void checkUser(User user) {
        if (user.getLogin().trim().contains(" ")) {
            log.error("Error! Login contains space");
            throw new ValidationException("Incorrect login");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
