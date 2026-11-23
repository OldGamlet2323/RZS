package com.ua.movielib.web.dto;

public record MovieDetailDto(
        Long id,
        String title,
        String director,
        Integer year,
        String genre,
        Double rating,
        String posterUrl
) {
}
