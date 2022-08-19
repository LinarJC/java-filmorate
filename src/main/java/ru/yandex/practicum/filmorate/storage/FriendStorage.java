package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

public interface FriendStorage {

    Integer mapRowToFriendId(ResultSet resultSet, int rowNum) throws SQLException;

    void addFriend(Integer userId, Integer friendId);

    Set<Integer> getUserFriendsById(Integer userId);

    Set<Integer> getUserAllFriends();

    void updateFriend(Integer userId, Set<Integer> friends);

    void deleteFriend(Integer userId, Integer friendId);

    Set<Integer> getCommonFriendList(Integer userId, Integer friendId);

    boolean isExist(Integer userId, Integer friendId);

}