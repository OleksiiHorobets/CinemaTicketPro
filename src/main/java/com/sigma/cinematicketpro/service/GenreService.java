package com.sigma.cinematicketpro.service;

import com.sigma.cinematicketpro.entity.tmdb.TMDBGenre;

import java.util.Map;

public interface GenreService {
   Map<Long, TMDBGenre> getAllGenresMap();
}