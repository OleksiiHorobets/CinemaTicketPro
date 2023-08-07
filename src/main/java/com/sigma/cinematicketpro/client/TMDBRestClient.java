package com.sigma.cinematicketpro.client;

import com.sigma.cinematicketpro.dto.tmdb.TMDBGenre;
import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;

import java.util.List;

public interface TMDBRestClient {
    List<TMDBMovie> getTrendingMovies();

    List<TMDBGenre> getAllGenres();
}