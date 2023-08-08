package com.sigma.cinematicketpro.client;

import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import com.sigma.cinematicketpro.entity.tmdb.TMDBGenre;

import java.util.List;

public interface TMDBRestClient {
    List<TMDBMovie> getTrendingMovies();

    List<TMDBGenre> getAllGenres();
}