package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Data
@Component
public class InMemoryUserStorage implements UserStorage{
    Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        validate(user);
        user.setId(users.values().size() + 1);
        if(StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        log.info("Добавлен новый пользователь: '{}', ID '{}', '{}'", user.getName(), user.getId(), user.getEmail());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        validate(user);
        if(!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с данным Id " + user.getId() + " не найден");
        } else if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        log.info("Внесены изменения в данные пользователя: '{}', ID '{}', '{}'",
                user.getName(), user.getId(), user.getEmail());
        return users.replace(user.getId(), user);
    }

    @Override
    public User removeUser(User user) {
        return users.remove(user.getId());
    }

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User findUser(Integer id) {
        final User user = users.getOrDefault(id, null);
        if (user == null) {
            throw new NotFoundException("Пользователь с данным Id " + id + " не найден");
        }
        return user;
    }

    public void addFriend(User user, User friend) {
        if(user == null) {
            throw new NotFoundException("Пользователь с данным Id не найден");
        } else if (friend == null){
            throw new NotFoundException("Друг с данным Id не найден");
        } else if (!user.getFriendIds().contains(friend.getId())){
            user.getFriendIds().add(friend.getId());
            friend.getFriendIds().add(user.getId());
        }
    }

    public void deleteFriend(User user, User friend) {
        if(user!=null && friend!=null && user.getFriendIds().contains(friend.getId())) {
            user.getFriendIds().remove(friend.getId());
            friend.getFriendIds().remove(user.getId());
        }
    }

    public Collection<User> findMutualFriends(int userId, int friendId) {
        User user = findUser(userId);
        User friend = findUser(friendId);
        Set<User> mutualFriends = new HashSet<>();
        for(Integer id : user.getFriendIds())
            if (friend.getFriendIds().contains(id)) mutualFriends.add(findUser(id));
        return mutualFriends;
    }

    public Collection<User> findAllFriends(int userId) {
        Set<User> friends = new HashSet<>();
        for(Integer id : findUser(userId).getFriendIds())  {
            friends.add(findUser(id));
        }
        return friends;
    }

    void validate(User user) {
        if (user.getLogin().contains(" ")) {
            throw new RuntimeException("Логин содержит пробелы.");
        }
        log.info("Проведена валидация данных пользователя: '{}'", user);
    }
}
