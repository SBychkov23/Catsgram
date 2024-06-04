package ru.yandex.practicum.catsgram.exception;

import java.io.IOException;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}