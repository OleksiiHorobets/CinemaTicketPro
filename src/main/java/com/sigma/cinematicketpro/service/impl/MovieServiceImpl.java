package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.client.TMDBRestClient;
import com.sigma.cinematicketpro.dto.MovieDTO;
import com.sigma.cinematicketpro.dto.tmdb.TMDBGenre;
import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import com.sigma.cinematicketpro.entity.Movie;
import com.sigma.cinematicketpro.exception.ResourceNotFoundException;
import com.sigma.cinematicketpro.mapper.MovieMapper;
import com.sigma.cinematicketpro.repository.MovieRepository;
import com.sigma.cinematicketpro.service.GenreService;
import com.sigma.cinematicketpro.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final GenreService genreService;
    private final MovieMapper movieMapper;
    private final TMDBRestClient tmdbRestClient;
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
        Map<Integer, TMDBGenre> genresMap = genreService.getAllGenresMap();

        List<TMDBMovie> trendingMovies = tmdbRestClient.getTrendingMovies();

        trendingMovies.forEach(tmdbMovie -> tmdbMovie.setGenres(
                        tmdbMovie.getGenres()
                                .stream()
                                .map(genre -> genresMap.get(genre.getId()))
                                .collect(Collectors.toSet())
                )
        );
        return trendingMovies;
    }
}