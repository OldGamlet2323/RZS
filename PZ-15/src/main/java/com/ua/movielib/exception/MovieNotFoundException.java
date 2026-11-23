package com.ua.movielib.exception;

/**
 * Фільм за id не знайдено.
 * Статус 404 і тіло відповіді формує GlobalExceptionHandler
 * (тому тут немає @ResponseStatus).
 */
public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(Long id) {
        super("Фільм з id=" + id + " не знайдено");
    }
}
