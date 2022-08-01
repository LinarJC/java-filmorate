package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class FilmorateApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(FilmorateApplication.class, args);
        UserController userController = context.getBean(UserController.class);
        FilmController filmController = context.getBean(FilmController.class);
    }

}
