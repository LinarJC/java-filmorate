package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DBException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Primary
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
@Override
    public Genre get(Integer id) {
        String sql = "select * from GENRES where GENRE_ID = ?";
        List<Genre> query = jdbcTemplate.query(sql, this::mapRowToGenre, id);
        if (query.size() == 0 ) {
            return null;
        } else if (query.size() != 1 ){
            throw new DBException(String.format("Ошибка при запросе данных из БД GENRES, id=%s.", id));
        }
        return query.get(0);
    }

    @Override
    public Set<Genre> getAll() {
        String sql = "select * from GENRES";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToGenre));
    }

    @Override
    public Set<Genre> getNames(Set<Genre> genresIds) {
        return genresIds.stream().map(genre -> get(genre.getId())).collect(Collectors.toSet());
    }
    @Override
    public Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }
}
