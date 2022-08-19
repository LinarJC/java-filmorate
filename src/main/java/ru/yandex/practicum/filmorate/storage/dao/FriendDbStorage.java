package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DBException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
@Primary
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sql = "insert into FRIENDS (user_id, friend_id) VALUES ( ?, ? )";
        if (jdbcTemplate.update(sql, userId, friendId) == 0) throw new DBException(
                String.format("Ошибка при добавлении в БД FRIENDS, userID=%s, friendID=%s.", userId, friendId));
    }

    @Override
    public Set<Integer> getUserFriendsById(Integer userId) {
        String sql = "select * from FRIENDS where USER_ID = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToFriendId, userId));
    }

    @Override
    public Set<Integer> getUserAllFriends(){
        String sql = "select * from FRIENDS";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToFriendId));
    }

    @Override
    public void updateFriend(Integer userId, Set<Integer> friends) {
        String sql = "update FRIENDS set FRIEND_ID = ?" +
                "where USER_ID = ?";
        if(friends == null || friends.isEmpty()) {
            return;
        }
        for (Integer friendId : friends) {
            if (jdbcTemplate.update(sql, friendId, userId) == 0)
                throw new DBException(String.format("Ошибка при обновлении данных в БД FRIENDS, id=%s.", friendId));
        }
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        String sql = "delete from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        if (jdbcTemplate.update(sql, userId, friendId) == 0) throw new DBException(
                String.format("Ошибка при удалении друга из БД FRIENDS, userID=%s, friendID=%s.",
                        userId, friendId));
    }

    @Override
    public Set<Integer> getCommonFriendList(Integer user1Id, Integer user2Id) {
        String sql = "select distinct F1.FRIEND_ID " +
                "from FRIENDS F1 join FRIENDS F2 on F1.FRIEND_ID = F2.FRIEND_ID " +
                "where F1.USER_ID = ? and F2.USER_ID = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToFriendId, user1Id, user2Id));
    }

    @Override
    public Integer mapRowToFriendId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("FRIEND_ID");
    }

    @Override
    public boolean isExist(Integer userId, Integer friendId) {
        String sql = "select * from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToFriendId, userId, friendId).size() == 1;
    }
}