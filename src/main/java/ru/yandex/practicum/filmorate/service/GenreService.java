package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {
    final GenreDbStorage genreDbStorage;

    public Genre getGenre(Integer id) {
        Genre result = genreDbStorage.get(id);
        if (result == null) throw new NotFoundException(String.format("genre ID=%s не найден.", id));
        return result;
    }

    public Set<Genre> getAllGenres() {
        return genreDbStorage.getAll();
    }
}
