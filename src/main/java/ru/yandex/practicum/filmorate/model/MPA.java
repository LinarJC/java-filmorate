package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class MPA {
    private Integer id;
    @NotBlank
    private String name;
}
