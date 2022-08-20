package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface MPAStorage {
    public MPA get(Integer id);

    public List<MPA> getAll();

    MPA mapRowToMpa(ResultSet resultSet, int numRow) throws SQLException;
}
