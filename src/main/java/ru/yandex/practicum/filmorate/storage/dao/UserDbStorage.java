package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DBException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        String sql = "insert into USERS(LOGIN, USER_NAME, EMAIL, BIRTHDAY) " +
                "VALUES (?, ?, ?, ?)";
        if (jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday()) == 0)
            throw new DBException(String.format("Ошибка при добавлении в БД USERS, id=%s.", user.getId()));
        sql = "select max(USER_ID) from USERS";
        user.setId(jdbcTemplate.queryForObject(sql, Integer.class));
        return user;
    }

    @Override
    public User findUser(Integer id) {
        String sql = "select USER_ID,USER_NAME,LOGIN,EMAIL,BIRTHDAY from USERS where USER_ID = ?";
        List<User> query = jdbcTemplate.query(sql, this::mapRowToUser, id);
        switch (query.size()) {
            case 0:
                return null;
            case 1:
                return query.get(0);
            default:
                throw new DBException(String.format("Ошибка при запросе данных из БД USERS, id=%s.", id));
        }
    }

    @Override
    public List<User> findAllUsers() {
        String sql = "select * from USERS";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User updateUser(User user) {
        String sql = "update USERS set USER_NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? where USER_ID = ?";
        if (jdbcTemplate.update(sql,
                user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId()) == 0)
            throw new DBException(String.format("Ошибка при обновлении данных в БД USERS, id=%s.", user.getId()));
        return user;
    }

    @Override
    public User removeUser(User user) {
        String sql = "delete from USERS where USER_ID = ?";
        if (jdbcTemplate.update(sql, user.getId()) == 0)
            throw new DBException(String.format("Ошибка при удалении из БД USERS, id=%s.", user.getId()));
        return user;
    }

    public void removeAll() {
        String sql = "delete from USERS";
        jdbcTemplate.update(sql);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .id(resultSet.getInt("USER_ID"))
                .name(resultSet.getString("USER_NAME"))
                .login(resultSet.getString("LOGIN"))
                .email(resultSet.getString("EMAIL"))
                .birthday(resultSet.getObject("BIRTHDAY", LocalDate.class))
                .build();
        user.setFriendIds(getUserFriendsById(user.getId()));
        return user;
    }

    public void addFriend(User user, User friend) {
        String sql = "insert into FRIENDS (USER_ID, FRIEND_ID) VALUES ( ?, ? )";
        if (jdbcTemplate.update(sql, user.getId(), friend.getId()) == 0)
            throw new DBException(String.format("Ошибка при добавлении друга БД USERS, , userID=%s, friendID=%s.",
                    user.getId(), friend.getId()));
    }

    public Set<Integer> getUserFriendsById(int id) {
        String sqlQuery = "SELECT * FROM FRIENDS WHERE USER_ID = ?";
        List<Integer> friends = jdbcTemplate.query(sqlQuery, this::mapRowToFriendId, id);
        return new LinkedHashSet<>(friends);
    }

    public void deleteFriend(User user, User friend) {
        String sql = "delete from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        if (jdbcTemplate.update(sql, user.getId(), friend.getId()) == 0) throw new DBException(
                String.format("Ошибка при удалении друга из БД FRIENDS, userID=%s, friendID=%s.",
                        user.getId(), friend.getId()));
    }

    public boolean isExist(User user) {
        String sql = "select * from USERS where USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, user.getId()).size() == 1;
    }

    public boolean isExist(User user, User friend) {
        String sql = "select * from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToFriendId, user.getId(), friend.getId()).size() == 1;
    }

    private Integer mapRowToFriendId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("FRIEND_ID");
    }
}