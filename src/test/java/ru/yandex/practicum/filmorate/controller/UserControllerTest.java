package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @Test
    void validate() {
        User user = new User();
        user.setName("");
        user.setEmail("user@yandex.ru");
        user.setLogin("User1");
        user.setBirthday(LocalDate.of(1995, 11, 1));

        assertAll(
                () -> assertNotNull(user),
                () -> assertNotNull(user.getEmail(), "Адрес электронной почты пользователя не пуст"),
                () -> assertTrue(user.getEmail().contains("@"),
                        "Адрес электронной почты пользователя содержит символ @"),
                () -> assertNotNull(user.getLogin(), "Логин пользователя не пуст"),
                () -> assertFalse(user.getLogin().contains(" "), "Логин не содержит пробелов"),
                () -> assertTrue(user.getBirthday().isBefore(LocalDate.now()),
                        "Дата рождения не в будущем")
        );
    }
}