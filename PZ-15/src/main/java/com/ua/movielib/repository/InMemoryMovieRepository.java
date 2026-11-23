package com.ua.movielib.repository;

import com.ua.movielib.domain.Movie;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryMovieRepository implements MovieRepository {

    private final Map<Long, Movie> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public List<Movie> findAll() {
        List<Movie> all = new ArrayList<>(storage.values());
        all.sort(Comparator.comparing(Movie::getId));
        return all;
    }

    @Override
    public Optional<Movie> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Movie save(Movie entity) {
        if (entity.getId() == null) {
            entity.setId(sequence.incrementAndGet());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
}
