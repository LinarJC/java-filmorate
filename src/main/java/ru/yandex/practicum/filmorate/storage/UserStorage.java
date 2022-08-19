package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    User removeUser(User user);

    void removeAll();

    Collection<User> findAllUsers();

    User findUser(Integer id);

    boolean isExist(User user);

}
