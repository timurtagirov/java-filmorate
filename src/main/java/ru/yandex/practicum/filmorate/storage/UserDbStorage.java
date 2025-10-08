package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.FriendsRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper userMapper;
    private final FriendsRowMapper friendMapper;

    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_FRIENDS_BY_USER_ID_QUERY = "SELECT friend_id FROM friends WHERE user_id = ?";
    private static final String DELETE_USER_FROM_FRIENDS_QUERY = "DELETE FROM friends WHERE user_id = ? OR friend_id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String DELETE_FRIENDSHIP_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";
    private static final String INSERT_USER_QUERY = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String INSERT_FRIENDS_QUERY = "MERGE INTO friends (user_id, friend_id) " +
            "KEY (user_id, friend_id) VALUES (?, ?)";
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String FIND_ALL_LIKES_QUERY = "SELECT user_id, friend_id FROM friends";

    @Override
    public Collection<User> getAllUsers() {
        // Получаем список фильмов с рейтингами
        List<User> users = jdbc.query(FIND_ALL_USERS_QUERY, userMapper);

        // получаем список всех лайков
        List<Friendship> friendships = jdbc.query(FIND_ALL_LIKES_QUERY, friendMapper);

        //превращаем список фильмов в Map для более быстрого поиска по id
        Map<Integer, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));
        // проходимся по всем лайкам и подставляем лайки в нужные фильмы
        for (Friendship friendship : friendships) {
            User user = userMap.get(friendship.getUserId());
            user.getFriends().add(friendship.getFriendId());
        }
        return userMap.values();
    }

    @Override
    public User createUser(User user) {
        // Добавляем в таблицу с пользователями
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);

        // Добавляем сгенерированный id пользователя в user
        Number key = Objects.requireNonNull(keyHolder.getKey(), "No key generated for films");
        int userId = key.intValue();
        user.setId(userId);

        // Добавляем в таблицу с друзьями
        if (!user.getFriends().isEmpty()) {
            jdbc.batchUpdate(
                    INSERT_FRIENDS_QUERY,
                    user.getFriends(),
                    user.getFriends().size(),
                    (ps, friendId) -> {
                        ps.setInt(1, userId);
                        ps.setInt(2, friendId);
                    }
            );
        }

        return user;
    }

    @Override
    public User updateUser(User user) {
        int rowsUpdated = jdbc.update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    @Override
    public User removeUser(int id) {
        User user = getUser(id);
        jdbc.update(DELETE_USER_FROM_FRIENDS_QUERY, id, id); // удаляем из таблицы с друзьями
        int rowsDeleted = jdbc.update(DELETE_USER_QUERY, id);  // удаляем из таблицы с юзерами
        if (rowsDeleted == 0) {
            throw new NotFoundException("A film with such ID doesn't exist");
        }
        return user;
    }

    @Override
    public User getUser(Integer id) {
        User user = new User();
        try {
            user = jdbc.queryForObject(FIND_USER_BY_ID_QUERY, userMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("A user with such ID doesn't exist: " + id);
        }
        List<Integer> friends = new ArrayList<>();
        try {
            friends = jdbc.queryForList(FIND_FRIENDS_BY_USER_ID_QUERY, Integer.class, id);
        } catch (EmptyResultDataAccessException ignored) {
        }
        if (!friends.isEmpty()) {
            user.getFriends().addAll(friends);
        }
        return user;
    }

    public void addFriends(int id, int friendId) {
        jdbc.update(INSERT_FRIENDS_QUERY, id, friendId);
    }

    public void removeFriendship(int id, int friendId) {
        jdbc.update(DELETE_FRIENDSHIP_QUERY, id, friendId);
    }
}
