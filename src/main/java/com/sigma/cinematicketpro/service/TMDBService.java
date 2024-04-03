package com.sigma.cinematicketpro.service;

import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import java.util.List;

public interface TMDBService {

  List<TMDBMovie> getTrendingMovies();
}