package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    InMemoryFilmStorage filmStorage;
    InMemoryUserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = (InMemoryFilmStorage) filmStorage;
        this.userStorage = (InMemoryUserStorage) userStorage;
    }

    public Film get(int filmId) {
        return filmStorage.findFilm(filmId);
    }

    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        filmStorage.updateFilm(film);
        return film;
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmStorage.findFilm(filmId), userStorage.findUser(userId));
    }

    public void deleteLike(int filmId, int userId) {
        filmStorage.deleteLike(filmStorage.findFilm(filmId), userStorage.findUser(userId));
    }

    public Collection<Film> findPopularFilms(int count) {
        return filmStorage.sortedListPopularFilms(count);
    }
}
