package com.ua.movielib.service.mapper;

import com.ua.movielib.domain.Movie;
import com.ua.movielib.web.dto.MovieCardDto;
import com.ua.movielib.web.dto.MovieDetailDto;
import com.ua.movielib.web.dto.MovieRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** МОДУЛЬНИЙ тест мапера (без Spring). */
class MovieMapperTest {

    private final MovieMapper mapper = new MovieMapper();
    private final Movie movie = new Movie(1L, "Inception", "Nolan", 2010, "Sci-Fi", 8.8, "/p.jpg");

    @Test
    void toCard_mapsKeyFields() {
        MovieCardDto dto = mapper.toCard(movie);

        assertThat(dto).isEqualTo(new MovieCardDto(1L, "Inception", 2010, "Sci-Fi", "/p.jpg"));
    }

    @Test
    void toDetail_mapsAllFields() {
        MovieDetailDto dto = mapper.toDetail(movie);

        assertThat(dto).isEqualTo(new MovieDetailDto(1L, "Inception", "Nolan", 2010, "Sci-Fi", 8.8, "/p.jpg"));
    }

    @Test
    void toDomain_doesNotSetId() {
        Movie result = mapper.toDomain(new MovieRequest("Dune", "Villeneuve", 2021, "Sci-Fi", 8.0, "/d.jpg"));

        assertThat(result.getId()).isNull();
        assertThat(result.getTitle()).isEqualTo("Dune");
        assertThat(result.getRating()).isEqualTo(8.0);
    }
}
