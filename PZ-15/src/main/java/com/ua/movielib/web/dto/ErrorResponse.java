package com.ua.movielib.web.dto;

import java.util.List;

/** Єдиний формат тіла помилки для всього API. */
public record ErrorResponse(
        int status,
        String error,
        String message,
        List<String> details
) {
}
