package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@Builder
public class MPA {
    private Integer id;
    private String name;
    @JsonIgnore
    private String description;
}
