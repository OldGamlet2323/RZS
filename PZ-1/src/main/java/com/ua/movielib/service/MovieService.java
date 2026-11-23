package com.ua.movielib.service;

import com.ua.movielib.web.dto.MovieCardDto;
import com.ua.movielib.web.dto.MovieDetailDto;
import com.ua.movielib.web.dto.MovieRequest;

import java.util.List;

public interface MovieService {

    List<MovieCardDto> findAll(String genre);

    MovieDetailDto findById(Long id);

    MovieDetailDto create(MovieRequest request);

    MovieDetailDto update(Long id, MovieRequest request);

    void delete(Long id);
}
