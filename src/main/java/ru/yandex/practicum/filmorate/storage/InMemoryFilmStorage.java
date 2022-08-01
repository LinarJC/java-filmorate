package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Data
@Component
public class InMemoryFilmStorage implements FilmStorage {
    Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        film.setId(films.size() + 1);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: '{}', ID '{}'", film.getName(), film.getId());
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

    public void addLike(Film film, User user) {
        Set<Integer> newUsersIds = film.getUserIds();
        newUsersIds.add(user.getId());
        film.setUserIds(newUsersIds);
        films.replace(film.getId(), film);
        log.info("Добавлен новый like: '{}', ID '{}'", film.getName(), film.getId());
    }

    public void deleteLike(Film film, User user) {
        Set<Integer> newUsersIds = film.getUserIds();
        newUsersIds.remove(user.getId());;
        film.setUserIds(newUsersIds);
        films.replace(film.getId(), film);
        log.info("Удалён like: '{}', ID '{}'", film.getName(), film.getId());
    }
}
