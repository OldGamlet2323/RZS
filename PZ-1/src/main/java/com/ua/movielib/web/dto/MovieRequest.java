package com.ua.movielib.web.dto;


public record MovieRequest(
        String title,
        String director,
        Integer year,
        String genre,
        Double rating,
        String posterUrl
) {
}
