package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface FilmStorage {

    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public Film removeFilm(Film film);

    public Collection<Film> findAllFilms();

    public Film findFilm(Integer id);


}
