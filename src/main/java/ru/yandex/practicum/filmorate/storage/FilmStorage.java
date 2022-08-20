package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film removeFilm(Film film);

    Collection<Film> findAllFilms();

    Film findFilm(Integer id);


    boolean isExist(Film film);

    Collection<Film> sortedListPopularFilms(int count);

    void updateRate(Integer filmId);
}
