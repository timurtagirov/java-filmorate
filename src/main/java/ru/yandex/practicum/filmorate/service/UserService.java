package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    public Collection<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public User getUser(Integer id) {
        return storage.getUser(id);
    }

    public User createUser(User user) {
        return storage.createUser(user);
    }

    public User updateUser(User user) {
        return storage.updateUser(user);
    }

    public void addTofriends(Integer firstUserId, Integer secondUserId) {
        log.debug("Adding a user to friends");
        log.trace("Looking for users");
        // Проверку на наличие юзера не стал писать, потому что getUser() в этом случае и так выбросит ошибку
        User firstUser = storage.getUser(firstUserId);
        User secondUser = storage.getUser(secondUserId);

        log.trace("Users are found. Now adding to friends");
        storage.addFriends(firstUserId, secondUserId);
        log.debug("Users are successfully added to friends of each other");
    }

    public void removeFromFriends(Integer id, Integer friendId) {
        log.debug("Removing a user from friends");
        log.trace("Looking for users");
        // Проверку на наличие юзера не стал писать, потому что getUser() в этом случае и так выбросит ошибку
        User firstUser = storage.getUser(id);
        User friend = storage.getUser(friendId);

        log.trace("Users are found. Checking that they are friends");
        if (firstUser.getFriends().contains(friendId)) {
            storage.removeFriendship(id, friendId);
            log.debug("Users are successfully removed from friends of each other");
        }
    }

    public Collection<User> getCommonFriends(Integer firstUserId, Integer secondUserId) {
        log.debug("Getting common friends of users");
        log.trace("Getting list of friends for users");
        // Проверку на наличие юзера не стал писать, потому что getUser() в этом случае и так выбросит ошибку
        Set<Integer> firstUserFriends = storage.getUser(firstUserId).getFriends();
        Set<Integer> secondUserFriends = storage.getUser(secondUserId).getFriends();

        log.debug("List of friends for users are successfully obtained. Getting common friends");
        return firstUserFriends
                .stream()
                .filter(secondUserFriends::contains)
                .map(storage::getUser)
                .collect(Collectors.toList());
    }

    public Collection<User> getFriends(Integer id) {
        log.debug("Getting a list of friends of user " + id);
        return storage.getUser(id).getFriends()
                .stream()
                .map(storage::getUser)
                .collect(Collectors.toList());
    }
}
