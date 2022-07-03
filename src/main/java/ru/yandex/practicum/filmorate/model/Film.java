package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private Integer id;
    @NotNull(message = "Название фильма не может быть пустым.")
    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;
    @Size(max = 200, message = "Длина описания превышает 200 символов.")
    private String description;
    @PastOrPresent(message = "Дата релиза не может быть в будущем.")
    private LocalDate releaseDate;
    @PositiveOrZero(message = "Продолжительность фильма должна быть положительной.")
    private Integer duration;
}
