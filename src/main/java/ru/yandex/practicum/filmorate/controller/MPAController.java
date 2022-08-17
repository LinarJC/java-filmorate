package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.Collection;

@RestController
@AllArgsConstructor
public class MPAController {
    final MPAService mpaService;

    @GetMapping("/mpa/{id}")
    public MPA get(@PathVariable  Integer id) {
        return mpaService.getMPA(id);
    }

    @GetMapping("/mpa")
    public Collection<MPA> getAll() {
        return mpaService.getAllMPA();
    }

}
