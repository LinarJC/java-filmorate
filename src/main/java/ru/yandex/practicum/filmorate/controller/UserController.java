package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        user.setId(users.size() + 1);
        validate(user);
        if(StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: '{}', ID '{}', '{}'", user.getName(), user.getId(), user.getEmail());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        if(users.containsKey(user.getId())) {
            validate(user);
            if (StringUtils.isBlank(user.getName())) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Внесены изменения в данные пользователя: '{}', ID '{}', '{}'",
                    user.getName(), user.getId(), user.getEmail());
            return user;
        } else {
            throw new RuntimeException("Пользователь с данным Id  не найден");
        }
    }

    void validate(User user) {
        if(user.getLogin().contains(" ")) {
            throw new RuntimeException("Логин содержит пробелы.");
        }
        log.info("Проведена валидация данных пользователя: '{}'", user);
    }
}
