package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Integer id;
    @NotNull(message = "Адрес электронной почты пользователя не может быть пустым.")
    @NotBlank(message = "Адрес электронной почты пользователя не может быть пустым.")
    @Email(message = "Адрес электронной почты пользователя некорректен.")
    private String email;
    private String name;
    @NotNull(message = "Логин пользователя не может быть пустым.")
    @NotBlank(message = "Логин пользователя не может быть пустым.")
    private String login;
    @Past(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;

    @JsonIgnore
    Set<Integer> friendIds;
}
