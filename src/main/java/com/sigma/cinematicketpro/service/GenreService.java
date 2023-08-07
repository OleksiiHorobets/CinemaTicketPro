package com.sigma.cinematicketpro.service;

import com.sigma.cinematicketpro.dto.tmdb.TMDBGenre;

import java.util.Map;

public interface GenreService {
   Map<Integer, TMDBGenre> getAllGenresMap();
}