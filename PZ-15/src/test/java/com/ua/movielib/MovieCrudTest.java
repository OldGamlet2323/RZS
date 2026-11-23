package com.ua.movielib;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MovieCrudTest {

    private static final String BODY =
            "{\"title\":\"Dune\",\"director\":\"Villeneuve\",\"year\":2021,\"genre\":\"Sci-Fi\",\"rating\":8.0,\"posterUrl\":\"/p.jpg\"}";

    @Autowired
    MockMvc mvc;

    @Test
    void listReturnsSeededCards() throws Exception {
        mvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].director").doesNotExist());
    }

    @Test
    void filterByGenre() throws Exception {
        mvc.perform(get("/api/movies").param("genre", "crime"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getOneAndMissing() throws Exception {
        mvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.director").value("Christopher Nolan"));
        mvc.perform(get("/api/movies/999")).andExpect(status().isNotFound());
    }

    @Test
    void createUpdateDelete() throws Exception {
        mvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Dune"));

        mvc.perform(put("/api/movies/3").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("Dune"));
        mvc.perform(put("/api/movies/999").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isNotFound());

        mvc.perform(delete("/api/movies/5")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/movies/5")).andExpect(status().isNotFound());
        mvc.perform(get("/api/movies/5")).andExpect(status().isNotFound());
    }

    @Test
    void invalidBodyGives400WithErrorResponse() throws Exception {
        mvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"director\":\"D\",\"year\":2000,\"genre\":\"G\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void notFoundHasStructuredBody() throws Exception {
        mvc.perform(get("/api/movies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
