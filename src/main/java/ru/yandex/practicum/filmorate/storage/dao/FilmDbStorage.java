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
        return film;
    }

    @Override
    public Film findFilm(Integer id){
        String sql = "select * from FILMS " +
                "join MPA_RATINGS on FILMS.MPA_ID = MPA_RATINGS.MPA_ID " +
                "where FILM_ID = ?";
        List<Film> query = jdbcTemplate.query(sql, this::mapRowToFilm, id);

        switch (query.size()) {
            case 0:
                return null;
            case 1:
                return query.get(0);
            default:
                throw new DBException(String.format("Ошибка при запросе данных из БД FILMS, id=%s.", id));
        }
    }

    @Override
    public Collection<Film> findAllFilms() {
        final String sql = "select * " +
                "from FILMS " +
                "join MPA_RATINGS on FILMS.MPA_ID = MPA_RATINGS.MPA_ID ";
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
        film.setUserIds(getFilmLikesById(film.getId()));
        getFilmGenre(film);
        return film;
    }

    public void addLike(Film film, User user) {
        String sql = "insert into LIKES(FILM_ID, USER_ID) " +
                "VALUES (?, ?)";
        if (jdbcTemplate.update(sql, film.getId(), user.getId()) == 0)
            throw new DBException(String.format("Ошибка при добавлении в БД LIKES, id=%s.", film.getId()));
    }

    public Set<Integer> getFilmLikesById(int id) {
        String sqlQuery = "SELECT * FROM LIKES WHERE FILM_ID = ?";
        List<Integer> userLikes = jdbcTemplate.query(sqlQuery, this::mapRowToLike, id);
        return new LinkedHashSet<>(userLikes);
    }

    public void deleteLike(Film film, User user) {
        String sql = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        if (jdbcTemplate.update(sql, film.getId(), user.getId()) == 0)
            throw new DBException(String.format("Ошибка при удалении из БД LIKES, film ID=%s, user ID=%s.",film.getId(), user.getId()));
    }
    private Integer mapRowToLike(ResultSet resultSet, int numRow) throws SQLException {
        return resultSet.getInt("USER_ID");
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

    public boolean isExist(Film film, Integer userId) {
        String sqlQuery = "SELECT * FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToLike, film.getId(), userId).size() == 1;
    }

    public Collection<Film> sortedListPopularFilms(int count) {
        return findAllFilms().stream()
                .sorted((o1, o2) -> o2.getUserIds().size() - o1.getUserIds().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
