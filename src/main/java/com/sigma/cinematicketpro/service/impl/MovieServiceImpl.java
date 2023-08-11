package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.dto.MovieDTO;
import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import com.sigma.cinematicketpro.entity.Movie;
import com.sigma.cinematicketpro.exception.ResourceNotFoundException;
import com.sigma.cinematicketpro.mapper.MovieMapper;
import com.sigma.cinematicketpro.repository.MovieRepository;
import com.sigma.cinematicketpro.service.MovieService;
import com.sigma.cinematicketpro.service.TMDBService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final TMDBService tmdbService;
    private static final String MOVIE_NOT_FOUND = "Movie not found: {%s}";

    @Override
    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(movieMapper::toDto)
                .toList();
    }

    @Override
    public MovieDTO getMovieById(String id) {
        return movieRepository.findById(id)
                .map(movieMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(MOVIE_NOT_FOUND.formatted(id)));
    }

    @Override
    public MovieDTO saveMovie(MovieDTO movieDto) {
        Movie movie = movieMapper.toEntity(movieDto);
        return movieMapper.toDto(movieRepository.save(movie));
    }

    @Override
    public MovieDTO updateMovie(String id, MovieDTO movieDto) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException(MOVIE_NOT_FOUND.formatted(id));
        }
        Movie movie = movieMapper.toEntity(movieDto);
        movie.setId(id);
        return movieMapper.toDto(movieRepository.save(movie));
    }

    @Override
    public void deleteMovieById(String id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException(MOVIE_NOT_FOUND.formatted(id));
        }
        movieRepository.deleteById(id);
    }

    @Override
    public List<TMDBMovie> getTrendingMovies() {
        return tmdbService.getTrendingMovies();
    }
}