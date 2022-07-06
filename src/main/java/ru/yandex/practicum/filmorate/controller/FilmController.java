package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрошен список фильмов: '{}'", films.values());
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        film.setId(films.size() + 1);
        validate(film);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: '{}', ID '{}'", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        if(films.containsKey(film.getId())) {
            validate(film);
            films.put(film.getId(), film);
            log.info("Внесены изменения в фильм: '{}', ID '{}'", film.getName(), film.getId());
            return film;
        } else {
            throw new RuntimeException("Фильм с данным Id  не найден");
        }
    }

    void validate(Film film) {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new DateTimeException("Указанная дата релиза не может быть ранее 28 декабря 1895 года.");
        }
        log.info("Проведена валидация объекта: '{}'", film);
    }
}
