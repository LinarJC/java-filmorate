package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DBException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@Primary
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String sql = "merge into LIKES(FILM_ID, USER_ID) " +
                "VALUES (?, ?)";
        if (jdbcTemplate.update(sql, filmId, userId) == 0) throw new DBException(
                String.format("Ошибка при добавлении в БД LIKES, filmID=%s, userID=%s.", filmId, userId));
    }

    @Override
    public Set<Integer> getFilmLikes(Integer id) {
        String sql = "SELECT * FROM LIKES WHERE FILM_ID = ?";
        List<Integer> userLikes = jdbcTemplate.query(sql, this::mapRowToLike, id);
        return new LinkedHashSet<>(userLikes);
    }

    @Override
    public Set<Integer> getAllFilmLikes() {
        String sql = "SELECT * FROM LIKES";
        List<Integer> userLikes = jdbcTemplate.query(sql, this::mapRowToLike);
        return new LinkedHashSet<>(userLikes);
    }

    @Override
    public void updateLike(Set<Integer> likes) {
        String sql = "update LIKES set USER_ID = ?" +
                "where FILM_ID = ?";
        if(likes == null || likes.isEmpty()) {
            return;
        }
        for (Integer id : likes) {
            if (jdbcTemplate.update(sql, id) == 0)
                throw new DBException(String.format("Ошибка при обновлении данных в БД FILMS, id=%s.", id));
        }
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sql = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        if (jdbcTemplate.update(sql, filmId, userId) == 0)
            throw new DBException(String.format("Ошибка при удалении из БД LIKES, film ID=%s, user ID=%s.",
                    filmId, userId));
    }

    @Override
    public Integer mapRowToLike(ResultSet resultSet, int numRow) throws SQLException {
        return resultSet.getInt("USER_ID");
    }

    @Override
    public boolean isExist(Integer filmId, Integer userId) {
        String sqlQuery = "SELECT * FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToLike, filmId, userId).size() == 1;
    }
}
