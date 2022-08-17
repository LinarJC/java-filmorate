package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DBException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Primary
public class MPADbStorage {
    private final JdbcTemplate jdbcTemplate;

    public MPA get(Integer id) {
        String sql = "select * from MPA_RATINGS where MPA_ID = ?";
        List<MPA> query = jdbcTemplate.query(sql, this::mapRowToMpa, id);
        switch (query.size()) {
            case 0: return null;
            case 1: return query.get(0);
            default: throw new DBException(String.format("Ошибка при запросе данных из БД MPA, id=%s.", id));
        }
    }

    public List<MPA> getAll() {
        String sql = "select * from MPA_RATINGS";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    private MPA mapRowToMpa(ResultSet resultSet, int numRow) throws SQLException {
        return MPA.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("MPA_NAME"))
                .build();
    }
}
