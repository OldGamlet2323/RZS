package com.ua.movielib.config;

import com.ua.movielib.domain.Movie;
import com.ua.movielib.repository.MovieRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Наповнює сховище стартовими фільмами при запуску (дані — у коді, не через curl). */
@Component
public class MovieSeeder implements ApplicationRunner {

    private final MovieRepository repository;

    public MovieSeeder(MovieRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        repository.save(new Movie(null, "Inception", "Christopher Nolan", 2010,
                "Sci-Fi", 8.8, "/posters/inception.jpg"));
        repository.save(new Movie(null, "The Godfather", "Francis Ford Coppola", 1972,
                "Crime", 9.2, "/posters/godfather.jpg"));
        repository.save(new Movie(null, "Spirited Away", "Hayao Miyazaki", 2001,
                "Animation", 8.6, "/posters/spirited-away.jpg"));
        repository.save(new Movie(null, "Interstellar", "Christopher Nolan", 2014,
                "Sci-Fi", 8.7, "/posters/interstellar.jpg"));
        repository.save(new Movie(null, "Pulp Fiction", "Quentin Tarantino", 1994,
                "Crime", 8.9, "/posters/pulp-fiction.jpg"));
    }
}
