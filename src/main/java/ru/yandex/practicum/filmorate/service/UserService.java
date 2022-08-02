package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    InMemoryUserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = (InMemoryUserStorage) userStorage;
    }

    public User get(int userId) {
        return userStorage.findUser(userId);
    }

    public Collection<User> getAll() {
        return userStorage.findAllUsers();
    }

    public User createUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        userStorage.updateUser(user);
        return user;
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userStorage.findUser(userId), userStorage.findUser(friendId));
    }

    public void deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userStorage.findUser(userId), userStorage.findUser(friendId));
    }

    public Collection<User> findMutualFriends(int userId, int friendId) {
        return userStorage.findMutualFriends(userId, friendId);
    }

    public Collection<User> findAllFriends(int userId) {
        return userStorage.findAllFriends(userId);
    }
}

