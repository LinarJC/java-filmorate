package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DBException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO FILMS(FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
                "VALUES (?, ?, ?, ?, ?)";
        if (jdbcTemplate.update(sql,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId()) == 0)
            throw new DBException(String.format("Ошибка при добавлении в БД FILMS, id=%s.", film.getId()));
        film.setId(jdbcTemplate.queryForObject("select max(FILM_ID) from FILMS", Integer.class));
        addFilmGenre(film);
        film.setRate(0);
        return film;
    }

    @Override
    public Film findFilm(Integer id){
        String sql = "select * from FILMS " +
                "join MPA_RATINGS on FILMS.MPA_ID = MPA_RATINGS.MPA_ID " +
                "where FILM_ID = ?";
        List<Film> query = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        if (query.size() == 0 ) {
            return null;
        } else if (query.size() != 1 ){
            throw new DBException(String.format("Ошибка при запросе данных из БД FILMS, id=%s.", id));
        }
        return query.get(0);
    }


    @Override
    public Collection<Film> findAllFilms() {
        final String sql = "select * " +
                "from FILMS, MPA_RATINGS " +
                "where FILMS.MPA_ID = MPA_RATINGS.MPA_ID";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film updateFilm(Film film) {

        String sql = "update FILMS set FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "where FILM_ID = ?";
        if (jdbcTemplate.update(sql,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(),
                film.getId()) == 0)
            throw new DBException(String.format("Ошибка при обновлении данных в БД FILMS, id=%s.", film.getId()));
        addFilmGenre(film);
        return findFilm(film.getId());
    }

    @Override
    public Film removeFilm(Film film) {
        String sql = "delete from FILMS where FILM_ID = ?";
        if (jdbcTemplate.update(sql, film.getId()) == 0)
            throw new DBException(String.format("Ошибка при удалении из БД FILMS, id=%s.", film.getId()));
        return film;
    }

    public void removeAllFilms() {
        jdbcTemplate.update("delete from FILMS");
    }

    private Film mapRowToFilm(ResultSet resultSet, int numRow) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getInt("DURATION"))
                .mpa(MPA.builder()
                        .id(resultSet.getInt("MPA_ID"))
                        .name(resultSet.getString("MPA_NAME"))
                        .build())
                .build();
        getFilmGenre(film);
        updateRate(resultSet.getInt("FILM_ID"));
        return film;
    }

    public void addFilmGenre(Film film) {
        String sql = "delete from FILMS_GENRES where FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());
        if(film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        for (Genre genre : film.getGenres()) {
            sql = "insert into FILMS_GENRES(FILM_ID, GENRE_ID) " +
                    "VALUES (?, ?)";
            if (jdbcTemplate.update(sql, film.getId(), genre.getId()) == 0)
                throw new DBException(String.format("Ошибка при добавлении в БД FILMS_GENRES, id=%s.", film.getId()));
        }
    }

    public Set<Genre> getFilmGenre(Film film) {
        String sql = "SELECT * FROM FILMS_GENRES " +
                "JOIN GENRES on FILMS_GENRES.GENRE_ID = GENRES.GENRE_ID " +
                "WHERE FILM_ID = ?";
        Set<Genre> genres = new ArrayList<>(jdbcTemplate.query(sql, this::mapRowToGenre,
                film.getId())).stream().sorted(Comparator.comparingInt(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        film.setGenres(genres);
        return genres;
    }
    private Genre mapRowToGenre(ResultSet resultSet, int numRow) throws SQLException {
        return new Genre(resultSet.getInt("GENRE_ID"), resultSet.getString("GENRE_NAME"));
    }
    public boolean isExist(Film film) {
        return findFilm(film.getId()) != null;
    }

    public Collection<Film> sortedListPopularFilms(int count) {
        String sqlQuery = "select * from FILMS join MPA_RATINGS on FILMS.MPA_ID = MPA_RATINGS.MPA_ID " +
                "order by RATE DESC limit ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public void updateRate(Integer filmId) {
        String sql = "update FILMS f " +
                "set RATE = (select count(l.USER_ID) from LIKES l " +
                "where l.FILM_ID = f.FILM_ID) where FILM_ID = ?";
        jdbcTemplate.update(sql, filmId);
    }
}