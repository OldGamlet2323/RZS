package com.ua.movielib.repository;

import com.ua.movielib.domain.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {

    List<Movie> findAll();

    Optional<Movie> findById(Long id);

    Movie save(Movie entity);

    /** @return true, якщо запис існував і був видалений. */
    boolean deleteById(Long id);
}
