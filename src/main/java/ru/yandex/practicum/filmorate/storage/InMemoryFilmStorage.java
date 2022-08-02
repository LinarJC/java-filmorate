package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
@Component
public class InMemoryFilmStorage implements FilmStorage {
    Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        validate(film);
        film.setId(films.size() + 1);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: '{}', ID '{}'", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validate(film);
        isExist(film);
        log.info("Внесены изменения в фильм: '{}', ID '{}'", film.getName(), film.getId());
        return films.replace(film.getId(), film);
    }

    @Override
    public Film removeFilm(Film film) {
        return films.remove(film.getId());
    }

    @Override
    public Collection<Film> findAllFilms() {
        log.info("Запрошен список фильмов: '{}'", films.values());
        return films.values();
    }

    @Override
    public Film findFilm(Integer id) {
        final Film film = films.getOrDefault(id, null);
        if (film == null) {
            throw new NotFoundException("Фильм с данным Id  не найден");
        }
        log.info("Запрошен фильм: '{}'", films.getOrDefault(id, null));
        return film;
    }

    public void addLike(Film film, User user) {
        if(film!=null && user!=null && !film.getUserIds().contains(user.getId())) {
            Set<Integer> newUsersIds = film.getUserIds();
            newUsersIds.add(user.getId());
            film.setUserIds(newUsersIds);
            films.replace(film.getId(), film);
            log.info("Добавлен новый like: '{}', ID '{}'", film.getName(), film.getId());
        } else {
            throw new NotFoundException("Фильм или пользователь с данными Id  не найдены");
        }
    }

    public void deleteLike(Film film, User user) {
        if(film!=null && user!=null && film.getUserIds().contains(user.getId())) {
            Set<Integer> newUsersIds = film.getUserIds();
            newUsersIds.remove(user.getId());;
            film.setUserIds(newUsersIds);
            films.replace(film.getId(), film);
            log.info("Удалён like: '{}', ID '{}'", film.getName(), film.getId());
        } else {
            throw new NotFoundException("Фильм или пользователь с данными Id  не найдены");
        }
    }

    void isExist(Film film) {
        if(!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с данным Id  не найден");
        }
    }

    public Collection<Film> sortedListPopularFilms(int count) {
        Collection<Film> sortedList = findAllFilms().stream()
                .sorted((o1, o2) -> o2.getUserIds().size() - o1.getUserIds().size())
                .limit(count)
                .collect(Collectors.toList());
        log.info("Топ '{}' фильмов: '{}'", count, sortedList);
        return sortedList;
    }

    void validate(Film film) {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new DateTimeException("Указанная дата релиза не может быть ранее 28 декабря 1895 года.");
        }
        log.info("Проведена валидация объекта: '{}'", film);
    }
}
