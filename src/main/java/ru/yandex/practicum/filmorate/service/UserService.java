package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    private final FriendStorage friendStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public User get(int userId) {
        final User user = userStorage.findUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с данным Id не найден");
        }
        return user;
    }

    public Collection<User> getAll() {
        return userStorage.findAllUsers();
    }

    public User createUser(User user) {
        validate(user);
        user.setId(userStorage.findAllUsers().size() + 1);
        if(StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        log.info("Добавлен новый пользователь: '{}', ID '{}', '{}'", user.getName(), user.getId(), user.getEmail());
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        validate(user);
        if(!userStorage.isExist(user)) {
            throw new NotFoundException("Пользователь с данным Id " + user.getId() + " не найден");
        } else if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        userStorage.updateUser(user);
        log.info("Внесены изменения в данные пользователя: '{}', ID '{}', '{}'",
                user.getName(), user.getId(), user.getEmail());
        return user;
    }

    public void addFriend(int userId, int friendId) {
        final User user = userStorage.findUser(userId);
        final User friend = userStorage.findUser(friendId);
        if(user == null) {
            throw new NotFoundException("Пользователь с данным Id " + userId + " не найден");
        } else if (friend == null){
            throw new NotFoundException("Друг с данным Id " + friendId + " не найден");
        } else if (!friendStorage.isExist(userId, friendId)){
            friendStorage.addFriend(userId, friendId);
        }
    }

    public void deleteFriend(int userId, int friendId) {
        final User user = userStorage.findUser(userId);
        final User friend = userStorage.findUser(friendId);
        if(user!=null && friend!=null && friendStorage.isExist(userId, friendId)) {
            friendStorage.deleteFriend(userId, friendId);
        }
    }

    public Collection<User> findMutualFriends(int userId, int friendId) {
        final User friend = userStorage.findUser(friendId);
        Set<User> mutualFriends = new HashSet<>();
        for(Integer id : friendStorage.getUserFriendsById(userId))
            if (friendStorage.getUserFriendsById(friendId).contains(id)) mutualFriends.add(userStorage.findUser(id));
        return mutualFriends;
    }

    public Collection<User> findAllFriends(int userId) {
        Set<User> friends = new HashSet<>();
        for(Integer id : friendStorage.getUserFriendsById(userId))  {
            friends.add(userStorage.findUser(id));
        }
        return friends;
    }
    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            throw new RuntimeException("Логин содержит пробелы.");
        }
        log.info("Проведена валидация данных пользователя: '{}'", user);
    }
}

