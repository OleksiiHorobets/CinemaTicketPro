package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.client.TMDBRestClient;
import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import com.sigma.cinematicketpro.entity.tmdb.TMDBGenre;
import com.sigma.cinematicketpro.service.GenreService;
import com.sigma.cinematicketpro.service.TMDBService;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TMDBServiceImpl implements TMDBService {

  private final GenreService genreService;
  private final TMDBRestClient tmdbRestClient;

  @Override
  public List<TMDBMovie> getTrendingMovies() {
    Map<Long, TMDBGenre> genresMap = genreService.getAllGenresMap();
    List<TMDBMovie> trendingMovies = tmdbRestClient.getTrendingMovies();

    trendingMovies.forEach(mapMovieGenres(genresMap));
    return trendingMovies;
  }

  private Consumer<TMDBMovie> mapMovieGenres(Map<Long, TMDBGenre> genresMap) {
    return tmdbMovie -> tmdbMovie.setGenres(
        tmdbMovie.getGenres()
            .stream()
            .map(genre -> genresMap.get(genre.getId()))
            .collect(Collectors.toSet())
    );
  }
}