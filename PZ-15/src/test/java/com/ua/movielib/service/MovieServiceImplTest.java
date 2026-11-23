package com.ua.movielib.service;

import com.ua.movielib.domain.Movie;
import com.ua.movielib.exception.MovieNotFoundException;
import com.ua.movielib.repository.MovieRepository;
import com.ua.movielib.service.mapper.MovieMapper;
import com.ua.movielib.web.dto.MovieCardDto;
import com.ua.movielib.web.dto.MovieDetailDto;
import com.ua.movielib.web.dto.MovieRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** МОДУЛЬНИЙ тест: сервіс ізольовано, репозиторій — mock, маппер — справжній (простий). */
class MovieServiceImplTest {

    private final MovieRepository repository = mock(MovieRepository.class);
    private final MovieMapper mapper = new MovieMapper();
    private final MovieService service = new MovieServiceImpl(repository, mapper);

    private static Movie inception() {
        return new Movie(1L, "Inception", "Nolan", 2010, "Sci-Fi", 8.8, "/p.jpg");
    }

    private static MovieRequest request() {
        return new MovieRequest("Dune", "Villeneuve", 2021, "Sci-Fi", 8.0, "/d.jpg");
    }

    @Test
    void findById_returnsDetailDto() {
        when(repository.findById(1L)).thenReturn(Optional.of(inception()));

        MovieDetailDto dto = service.findById(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.title()).isEqualTo("Inception");
        assertThat(dto.director()).isEqualTo("Nolan");
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void findAll_withoutGenre_returnsAllCards() {
        when(repository.findAll()).thenReturn(List.of(inception(),
                new Movie(2L, "Godfather", "Coppola", 1972, "Crime", 9.2, null)));

        List<MovieCardDto> cards = service.findAll(null);

        assertThat(cards).hasSize(2);
    }

    @Test
    void findAll_withGenre_filtersIgnoringCase() {
        when(repository.findAll()).thenReturn(List.of(inception(),
                new Movie(2L, "Godfather", "Coppola", 1972, "Crime", 9.2, null)));

        List<MovieCardDto> cards = service.findAll("crime");

        assertThat(cards).extracting(MovieCardDto::title).containsExactly("Godfather");
    }

    @Test
    void create_savesAndReturnsWithAssignedId() {
        when(repository.save(any(Movie.class))).thenAnswer(inv -> {
            Movie m = inv.getArgument(0);
            m.setId(42L);
            return m;
        });

        MovieDetailDto dto = service.create(request());

        assertThat(dto.id()).isEqualTo(42L);
        assertThat(dto.title()).isEqualTo("Dune");
        verify(repository).save(any(Movie.class));
    }

    @Test
    void update_keepsIdFromPath() {
        when(repository.findById(1L)).thenReturn(Optional.of(inception()));
        when(repository.save(any(Movie.class))).thenAnswer(inv -> inv.getArgument(0));

        MovieDetailDto dto = service.update(1L, request());

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.title()).isEqualTo("Dune");
    }

    @Test
    void update_throwsWhenNotFound_andDoesNotSave() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999L, request()))
                .isInstanceOf(MovieNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_callsRepository() {
        when(repository.deleteById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(repository.deleteById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(MovieNotFoundException.class);
    }
}
