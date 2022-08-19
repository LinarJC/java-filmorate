package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public interface LikeStorage {

    void addLike(Integer filmId, Integer userId);

    Set<Integer> getFilmLikes(Integer id);

    Set<Integer> getAllFilmLikes();

    void updateLike(Set<Integer> likes);

    void deleteLike(Integer filmId, Integer userId);

    Integer mapRowToLike(ResultSet resultSet, int rowNum) throws SQLException;

    boolean isExist(Integer filmId, Integer userId);
}