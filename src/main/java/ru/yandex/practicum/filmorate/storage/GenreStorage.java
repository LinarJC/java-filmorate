package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public interface GenreStorage {

    public Genre get(Integer id);

    public Set<Genre> getAll();

    public Set<Genre> getNames(Set<Genre> genresIds);

    Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException;
}