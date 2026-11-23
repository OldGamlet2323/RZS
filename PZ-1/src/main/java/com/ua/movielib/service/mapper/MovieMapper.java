package com.ua.movielib.service.mapper;

import com.ua.movielib.domain.Movie;
import com.ua.movielib.web.dto.MovieCardDto;
import com.ua.movielib.web.dto.MovieDetailDto;
import com.ua.movielib.web.dto.MovieRequest;
import org.springframework.stereotype.Component;

/** Перетворення Movie ↔ DTO. Окремо від сервісу (SRP). */
@Component
public class MovieMapper {

    public MovieCardDto toCard(Movie movie) {
        return new MovieCardDto(movie.getId(), movie.getTitle(), movie.getYear(),
                movie.getGenre(), movie.getPosterUrl());
    }

    public MovieDetailDto toDetail(Movie movie) {
        return new MovieDetailDto(movie.getId(), movie.getTitle(), movie.getDirector(),
                movie.getYear(), movie.getGenre(), movie.getRating(), movie.getPosterUrl());
    }

    /** id не встановлюється: його призначить репозиторій або сервіс (при update). */
    public Movie toDomain(MovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.title());
        movie.setDirector(request.director());
        movie.setYear(request.year());
        movie.setGenre(request.genre());
        movie.setRating(request.rating());
        movie.setPosterUrl(request.posterUrl());
        return movie;
    }
}
