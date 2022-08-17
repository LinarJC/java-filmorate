package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dao.MPADbStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MPAService {
    final MPADbStorage mpaDbStorage;

    public MPA getMPA(Integer id) {
        MPA result = mpaDbStorage.get(id);
        if (result == null) throw new NotFoundException(String.format("mpaID=%s не найден.", id));
        return result;
    }

    public Collection<MPA> getAllMPA() {
        return mpaDbStorage.getAll();
    }
}
