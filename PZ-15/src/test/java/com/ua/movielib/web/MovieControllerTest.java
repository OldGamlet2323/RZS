package com.ua.movielib.web;

import com.ua.movielib.exception.MovieNotFoundException;
import com.ua.movielib.service.MovieService;
import com.ua.movielib.web.dto.MovieCardDto;
import com.ua.movielib.web.dto.MovieDetailDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ІНТЕГРАЦІЙНИЙ тест веб-шару: @WebMvcTest піднімає лише контролер + GlobalExceptionHandler,
 * сервіс замокано (@MockBean).
 */
@WebMvcTest(MovieController.class)
class MovieControllerTest {

    private static final String VALID_BODY =
            "{\"title\":\"Dune\",\"director\":\"Villeneuve\",\"year\":2021,\"genre\":\"Sci-Fi\",\"rating\":8.0}";

    private static final MovieDetailDto DUNE =
            new MovieDetailDto(6L, "Dune", "Villeneuve", 2021, "Sci-Fi", 8.0, null);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    // ---------- GET ----------

    @Test
    void list_returnsCards() throws Exception {
        when(movieService.findAll(null)).thenReturn(List.of(
                new MovieCardDto(1L, "Inception", 2010, "Sci-Fi", "/p.jpg")));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Inception"))
                .andExpect(jsonPath("$[0].director").doesNotExist());
    }

    @Test
    void list_passesGenreFilter() throws Exception {
        when(movieService.findAll("Crime")).thenReturn(List.of());

        mockMvc.perform(get("/api/movies").param("genre", "Crime"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(movieService).findAll("Crime");
    }

    @Test
    void getOne_returnsDetail() throws Exception {
        when(movieService.findById(1L)).thenReturn(
                new MovieDetailDto(1L, "Inception", "Nolan", 2010, "Sci-Fi", 8.8, "/p.jpg"));

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.director").value("Nolan"))
                .andExpect(jsonPath("$.rating").value(8.8));
    }

    @Test
    void getOne_returns404WithStructuredBody() throws Exception {
        when(movieService.findById(999L)).thenThrow(new MovieNotFoundException(999L));

        mockMvc.perform(get("/api/movies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Фільм з id=999 не знайдено"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void getOne_returns400WhenIdIsNotNumber() throws Exception {
        mockMvc.perform(get("/api/movies/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // ---------- POST ----------

    @Test
    void create_valid_returns201() throws Exception {
        when(movieService.create(any())).thenReturn(DUNE);

        mockMvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.title").value("Dune"));
    }

    @Test
    void create_blankTitle_returns400WithDetails() throws Exception {
        String body = "{\"title\":\"\",\"director\":\"V\",\"year\":2021,\"genre\":\"Sci-Fi\",\"rating\":8.0}";

        mockMvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Помилка валідації"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[0]").value("title: назва обов'язкова"));
        verify(movieService, never()).create(any());
    }

    @Test
    void create_negativeYearAndRatingOutOfRange_returnsAllProblems() throws Exception {
        String body = "{\"title\":\"X\",\"director\":\"V\",\"year\":-5,\"genre\":\"G\",\"rating\":11.5}";

        mockMvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.length()").value(2));
    }

    @Test
    void create_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON).content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Некоректне тіло запиту"));
    }

    // ---------- PUT ----------

    @Test
    void update_valid_returns200() throws Exception {
        when(movieService.update(eq(6L), any())).thenReturn(DUNE);

        mockMvc.perform(put("/api/movies/6").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6));
    }

    @Test
    void update_missing_returns404() throws Exception {
        when(movieService.update(eq(999L), any())).thenThrow(new MovieNotFoundException(999L));

        mockMvc.perform(put("/api/movies/999").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void update_invalid_returns400() throws Exception {
        String body = "{\"title\":\" \",\"director\":\"\",\"year\":2021,\"genre\":\"G\"}";

        mockMvc.perform(put("/api/movies/6").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isArray());
        verify(movieService, never()).update(any(), any());
    }

    // ---------- DELETE ----------

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isNoContent());
        verify(movieService).delete(1L);
    }

    @Test
    void delete_missing_returns404() throws Exception {
        doThrow(new MovieNotFoundException(999L)).when(movieService).delete(999L);

        mockMvc.perform(delete("/api/movies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Фільм з id=999 не знайдено"));
    }
}
