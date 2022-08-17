package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService {
    FilmDbStorage filmStorage;
    UserDbStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = (FilmDbStorage) filmStorage;
        this.userStorage = (UserDbStorage) userStorage;
    }

    public Film get(int filmId) {
        final Film film = filmStorage.findFilm(filmId);
        if(film == null)  {
            throw new NotFoundException("Фильм с данным Id  не найден");
        }
        log.info("Запрошен фильм: '{}'", film);
        return film;
    }

    public Collection<Film> findAllFilms() {
        log.info("Запрошен список фильмов: '{}'", filmStorage.findAllFilms());
        return filmStorage.findAllFilms();
    }

    public Film addFilm(Film film) {
        validate(film);
        log.info("Добавлен новый фильм: '{}', ID '{}'", film.getName(), film.getId());
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        if(!filmStorage.isExist(film)) {
            throw new NotFoundException("Фильм с данным Id  не найден");
        }
        log.info("Внесены изменения в фильм: '{}', ID '{}'", film.getName(), film.getId());
        return filmStorage.updateFilm(film);
    }

    public void addLike(int filmId, int userId) {
        final Film film = filmStorage.findFilm(filmId);
        final User user = userStorage.findUser(userId);
        if (film != null && user != null && !filmStorage.isExist(film, userId)) {
            Set<Integer> newUsersIds = film.getUserIds();
            newUsersIds.add(userId);
            film.setUserIds(newUsersIds);
            filmStorage.addLike(film, user);
            log.info("Добавлен новый like: '{}', ID '{}'", film.getName(), film.getId());
        } else {
            throw new NotFoundException("Фильм с данным Id  не найден");
        }
    }

    public void deleteLike(int filmId, int userId) {
        final Film film = filmStorage.findFilm(filmId);
        final User user = userStorage.findUser(userId);
        if(film!=null && user!=null && filmStorage.isExist(film, userId)) {
            Set<Integer> newUsersIds = film.getUserIds();
            newUsersIds.remove(userId);;
            film.setUserIds(newUsersIds);
            filmStorage.deleteLike(film, user);
            log.info("Удалён like: '{}', ID '{}'", film.getName(), film.getId());
        } else {
            throw new NotFoundException("Фильм с данным Id  не найден");
        }
    }

    public Collection<Film> findPopularFilms(int count) {
        log.info("Топ '{}' фильмов: '{}'", count, filmStorage.sortedListPopularFilms(count));
        return filmStorage.sortedListPopularFilms(count);
    }

    void validate(Film film) {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new DateTimeException("Указанная дата релиза не может быть ранее 28 декабря 1895 года.");
        }
        log.info("Проведена валидация объекта: '{}'", film);
    }
}
