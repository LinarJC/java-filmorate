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
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        return films.replace(film.getId(), film);
    }

    @Override
    public Film removeFilm(Film film) {
        return films.remove(film.getId());
    }

    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @Override
    public Film findFilm(Integer id) {
        return films.getOrDefault(id, null);
    }

    public void addLike(Film film) {
        films.replace(film.getId(), film);
    }

    public void deleteLike(Film film) {
        films.replace(film.getId(), film);
    }

    public boolean isExist(Film film) {
        return films.containsKey(film.getId());
    }

    public boolean isExist(Film film, Integer userId) {
        return film.getUserIds().contains(userId);
    }

    public Collection<Film> sortedListPopularFilms(int count) {
        return findAllFilms().stream()
                .sorted((o1, o2) -> o2.getUserIds().size() - o1.getUserIds().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
