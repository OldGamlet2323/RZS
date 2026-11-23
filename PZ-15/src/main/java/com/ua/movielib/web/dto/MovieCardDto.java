package com.ua.movielib.web.dto;

public record MovieCardDto(
        Long id,
        String title,
        Integer year,
        String genre,
        String posterUrl
) {
}
