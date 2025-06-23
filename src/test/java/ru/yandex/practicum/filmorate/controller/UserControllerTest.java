package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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

        //Shouldn't add user
        user.setEmail(" ");
        userController.createUser(user);
        assertEquals(1, userController.getAllUsers().size());

        //Shouldn't add user
        user.setEmail("someemail");
        userController.createUser(user);
        assertEquals(1, userController.getAllUsers().size());

        //Shouldn't add user because of space in login
        user.setEmail("someemail@");
        userController.createUser(user);
        assertEquals(2, userController.getAllUsers().size());
        user.setLogin("John Smith");
        userController.createUser(user);
        assertEquals(2, userController.getAllUsers().size());

        //Shouldn't add user because of birthday
        user.setLogin("JohnSmith");
        userController.createUser(user);
        assertEquals(3, userController.getAllUsers().size());
        user.setBirthday(LocalDate.now().plusDays(1));
        userController.createUser(user);
        assertEquals(3, userController.getAllUsers().size());
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

        //Shouldn't update email
        User newUser = new User();
        newUser.setEmail("someemail@gmail.com");
        newUser.setLogin("JohnSmith");
        newUser.setName("John");
        newUser.setBirthday(LocalDate.of(2001, 7, 15));
        newUser.setId(1);
        userController.updateUser(newUser);
        assertEquals("someemail@gmail.com", userController.getAllUsers().stream().toList().getFirst().getEmail());

        //Shouldn't update email
        newUser.setEmail("someemail");
        userController.updateUser(newUser);
        assertEquals("someemail@gmail.com", userController.getAllUsers().stream().toList().getFirst().getEmail());

        //Shouldn't add user because of space in login
        newUser.setEmail("someemail@yahoo.com");
        userController.updateUser(newUser);
        assertEquals("someemail@yahoo.com", userController.getAllUsers().stream().toList().getFirst().getEmail());
        newUser.setLogin("John Smith");
        userController.updateUser(newUser);
        assertEquals("JohnSmith", userController.getAllUsers().stream().toList().getFirst().getLogin());

        //Shouldn't add user because of birthday
        newUser.setLogin("JohnSmithWithBirthdayInTheFuture");
        newUser.setBirthday(LocalDate.now().plusDays(1));
        userController.updateUser(newUser);
        assertEquals("JohnSmith", userController.getAllUsers().stream().toList().getFirst().getLogin());
    }
}
