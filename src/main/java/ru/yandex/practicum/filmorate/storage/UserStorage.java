package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface UserStorage {
    final Map<Integer, User> users = new HashMap<>();

    public User addUser(User user);

    public User updateUser(User user);

    public User removeUser(User user);

    public Collection<User> findAllUsers();

    public User findUser(Integer id);
}
