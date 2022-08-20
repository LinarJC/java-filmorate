package ru.yandex.practicum.filmorate.exception;

public class DBException extends RuntimeException {
        public DBException(String message) {
            super(message);
        }
}
