package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    @Test
    public void testPostUser() {
        User user = new User();
        user.setEmail("someemail@gmail.com");
        user.setLogin("JohnSmith");
        user.setName("John");
        user.setBirthday(LocalDate.of(2001, 7, 15));

        //shouldAddUser
        UserController userController = new UserController();
        userController.createUser(user);
        assertEquals(1, userController.getAllUsers().size());

        //Shouldn't add user because of space in login
        user.setEmail("someemail@");
        userController.createUser(user);
        assertEquals(2, userController.getAllUsers().size());
        user.setLogin("John Smith");
        assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
    }

    @Test
    public void testPutUser() {
        User user = new User();
        user.setEmail("someemail@gmail.com");
        user.setLogin("JohnSmith");
        user.setName("John");
        user.setBirthday(LocalDate.of(2001, 7, 15));

        //shouldAddUser
        UserController userController = new UserController();
        userController.createUser(user);
        assertEquals(1, userController.getAllUsers().size());
        assertEquals("someemail@gmail.com", userController.getAllUsers().stream().toList().getFirst().getEmail());

        //Shouldn't add user because of space in login
        User newUser = new User();
        newUser.setLogin("JohnSmith");
        newUser.setName("John");
        newUser.setBirthday(LocalDate.of(2001, 7, 15));
        newUser.setId(1);
        newUser.setEmail("someemail@yahoo.com");
        userController.updateUser(newUser);
        assertEquals("someemail@yahoo.com", userController.getAllUsers().stream().toList().getFirst().getEmail());
        newUser.setLogin("John Smith");
        assertThrows(ValidationException.class, () -> {
            userController.updateUser(newUser);
        });
    }
}
