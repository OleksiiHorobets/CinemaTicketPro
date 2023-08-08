package com.sigma.cinematicketpro.client.impl;

import com.sigma.cinematicketpro.client.TMDBRestClient;
import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import com.sigma.cinematicketpro.dto.tmdb.response.MovieTMDBApiResponse;
import com.sigma.cinematicketpro.dto.tmdb.response.TMDBGenresApiResponse;
import com.sigma.cinematicketpro.entity.tmdb.TMDBGenre;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class TMDBRestClientImpl implements TMDBRestClient {
    private static final String TRENDING_MOVIES_DAY_URI = "/trending/movie/day";
    private static final String MOVIE_GENRES_URI = "/genre/movie/list?language=en";

    @Qualifier("tmdbRestTemplate")
    private final RestTemplate tmdbRestTemplate;

    @Override
    public List<TMDBMovie> getTrendingMovies() {
        try {
            ResponseEntity<MovieTMDBApiResponse> response =
                    tmdbRestTemplate.getForEntity(TRENDING_MOVIES_DAY_URI, MovieTMDBApiResponse.class);

            return Optional.ofNullable(response.getBody())
                    .map(MovieTMDBApiResponse::getResults)
                    .orElseGet(Collections::emptyList);

        } catch (RestClientException apiException) {
            log.error("An error occurred in {}.getTrendingMovies(): GET call on {}",
                    this.getClass().getName(), TRENDING_MOVIES_DAY_URI, apiException);
            throw apiException;
        }
    }

    @Override
    public List<TMDBGenre> getAllGenres() {
        try {
            ResponseEntity<TMDBGenresApiResponse> response =
                    tmdbRestTemplate.getForEntity(MOVIE_GENRES_URI, TMDBGenresApiResponse.class);

            return Optional.ofNullable(response.getBody())
                    .map(TMDBGenresApiResponse::getGenres)
                    .orElseGet(Collections::emptyList);

        } catch (RestClientException apiException) {
            log.error("An error occurred in {}.getAllGenres(): GET call on {}",
                    this.getClass().getName(), MOVIE_GENRES_URI, apiException);
            throw apiException;
        }
    }
}
