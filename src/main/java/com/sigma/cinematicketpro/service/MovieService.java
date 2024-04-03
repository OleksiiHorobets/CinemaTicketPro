package com.sigma.cinematicketpro.service;

import com.sigma.cinematicketpro.dto.MovieDTO;
import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import java.util.List;

public interface MovieService {

  List<MovieDTO> getAllMovies();

  MovieDTO getMovieById(String id);

  MovieDTO saveMovie(MovieDTO movieDto);

  MovieDTO updateMovie(String id, MovieDTO movieDto);

  void deleteMovieById(String id);

  List<TMDBMovie> getTrendingMovies();
}