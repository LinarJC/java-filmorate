package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DBException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class MPADbStorage implements MPAStorage {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public MPA get(Integer id) {
        String sql = "select * from MPA_RATINGS where MPA_ID = ?";
        List<MPA> query = jdbcTemplate.query(sql, this::mapRowToMpa, id);
        if (query.size() == 0 ) {
            return null;
        } else if (query.size() != 1 ){
            throw new DBException(String.format("Ошибка при запросе данных из БД MPA_RATINGS, id=%s.", id));
        }
        return query.get(0);
    }
    @Override
    public List<MPA> getAll() {
        String sql = "select * from MPA_RATINGS";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    @Override
    public MPA mapRowToMpa(ResultSet resultSet, int numRow) throws SQLException {
        return MPA.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("MPA_NAME"))
                .build();
    }
}
