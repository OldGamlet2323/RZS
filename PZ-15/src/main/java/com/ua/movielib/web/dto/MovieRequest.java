package com.ua.movielib.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Вхідний DTO для POST/PUT. БЕЗ id — його задає сервер.
 * Правила валідації перевіряються завдяки @Valid у контролері.
 */
public record MovieRequest(
        @NotBlank(message = "назва обов'язкова") String title,
        @NotBlank(message = "режисер обов'язковий") String director,
        @Positive(message = "рік має бути додатним") Integer year,
        @NotBlank(message = "жанр обов'язковий") String genre,
        @DecimalMin(value = "0.0", message = "рейтинг не може бути меншим за 0")
        @DecimalMax(value = "10.0", message = "рейтинг не може бути більшим за 10")
        Double rating,
        String posterUrl
) {
}
