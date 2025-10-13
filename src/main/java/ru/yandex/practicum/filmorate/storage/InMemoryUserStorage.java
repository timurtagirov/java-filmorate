package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    HashMap<Integer, User> users = new HashMap<>();

    public Collection<User> getAllUsers() {
        log.debug("Getting the list of users");
        return users.values();
    }

    public User createUser(User user) {
        log.debug("Creating a user a film...");
        checkUser(user);
        log.trace("The data passed all checks. Adding the user to the library...");
        user.setId(IdGenerator.getNextId(users));
        users.put(user.getId(), user);
        log.debug("The user ", user.getName(), " has been successfully created");
        return user;
    }

    public User updateUser(User user) {
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

    public User removeUser(int id) {
        User user = users.get(id);
        log.debug("Removing a user...");
        if (!users.containsKey(id)) {
            log.error("Error! A user with such ID doesn't exist");
            throw new NotFoundException("A user with such ID doesn't exist");
        }
        log.trace("The data passed all checks. Updating it...");
        users.remove(id);
        log.debug("The user ", user.getName(), "  has been successfully removed");
        return user;
    }

    public User getUser(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("A user with such ID doesn't exist: " + id);
        }
        return users.get(id);
    }

    @Override
    public void addFriends(int id, int friendId) {
        users.get(id).getFriends().add(friendId);
    }

    @Override
    public void removeFriendship(int id, int friendId) {
        users.get(id).getFriends().remove(friendId);
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
