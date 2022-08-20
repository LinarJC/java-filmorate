package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
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
    private Integer rate;
    @NotNull(message = " MPA категория фильма не может быть пустой.")
    private MPA mpa;

    private Set<Genre> genres;
}
