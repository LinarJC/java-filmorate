package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
        final Film film = filmStorage.findFilm(filmId);
        if (film == null) {
            throw new NotFoundException("Film with id=" + filmId + " not found");
        }
        return film;
    }

    public Collection<Film> getAll() {
        log.info("Запрошен список фильмов: '{}'", filmStorage.findAllFilms());
        return filmStorage.findAllFilms();
    }

    public Film addFilm(Film film) {
        validate(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        if(!filmStorage.getFilms().containsKey(film.getId())) {
            throw new NotFoundException("Фильм с данным Id  не найден");
        }
            filmStorage.updateFilm(film);
            log.info("Внесены изменения в фильм: '{}', ID '{}'", film.getName(), film.getId());
            return film;
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.findFilm(filmId);
        User user = userStorage.findUser(userId);
        if(film!=null && user!=null && !film.getUserIds().contains(userId)) {
            filmStorage.addLike(film, user);
        }
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.findFilm(filmId);
        User user = userStorage.findUser(userId);
        if(film!=null && user!=null && film.getUserIds().contains(userId)) {
            filmStorage.deleteLike(film, user);
        } else {
            throw new NotFoundException("Фильм с данным Id  не найден");
        }
    }

    public List<Film> findTopAllFilms() {
        return filmStorage.findAllFilms().stream()
                .sorted((o1, o2) -> o2.getUserIds().size() - o1.getUserIds().size()).collect(Collectors.toList());
    }

    public List<Film> findTopCountFilms(int count) {
        List<Film> topFilms = filmStorage.findAllFilms().stream()
                .sorted((o1, o2) -> o2.getUserIds().size() - o1.getUserIds().size())
                .limit(count)
                .collect(Collectors.toList());
        log.info("топ фильмов: '{}'", topFilms);
        return topFilms;
    }

    void validate(Film film) {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new DateTimeException("Указанная дата релиза не может быть ранее 28 декабря 1895 года.");
        }
        log.info("Проведена валидация объекта: '{}'", film);
    }
}
