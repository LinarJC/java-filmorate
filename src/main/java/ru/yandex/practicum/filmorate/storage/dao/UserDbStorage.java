package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DBException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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
        if (query.size() == 0 ) {
            return null;
        } else if (query.size() != 1 ){
            throw new DBException(String.format("Ошибка при запросе данных из БД USERS, id=%s.", id));
        }
        return query.get(0);
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
        jdbcTemplate.update("delete from USERS");
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .name(resultSet.getString("USER_NAME"))
                .login(resultSet.getString("LOGIN"))
                .email(resultSet.getString("EMAIL"))
                .birthday(resultSet.getObject("BIRTHDAY", LocalDate.class))
                .build();
    }

    @Override
    public boolean isExist(User user) {
        String sql = "select * from USERS where USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, user.getId()).size() == 1;
    }
}