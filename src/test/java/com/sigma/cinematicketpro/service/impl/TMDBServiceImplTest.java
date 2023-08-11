package com.sigma.cinematicketpro.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sigma.cinematicketpro.client.TMDBRestClient;
import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import com.sigma.cinematicketpro.service.GenreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static com.sigma.cinematicketpro.TestUtils.getTMDBGenresIdGenreMap;
import static com.sigma.cinematicketpro.TestUtils.mapJsonToObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TMDBServiceImplTest {
    @Mock
    private GenreService genreService;
    @Mock
    private TMDBRestClient tmdbRestClient;
    @InjectMocks
    private TMDBServiceImpl sut;

    @Test
    void shouldCallTMDBRestClientAndGenreServiceWhenGetTrendingMovies() {
        sut.getTrendingMovies();

        verify(genreService, times(1)).getAllGenresMap();
        verify(tmdbRestClient, times(1)).getTrendingMovies();
        verifyNoMoreInteractions(genreService, tmdbRestClient);
    }

    @Test
    void shouldMapGenreIdToGenreTitleWhenGetTrendingMovies() throws IOException {
        List<TMDBMovie> moviesWithNoId = getTMDBMoviesWithoutGenresTitles();
        List<TMDBMovie> expectedTMDBMovieList = getTMDBMoviesWithMappedGenresTitles();

        when(tmdbRestClient.getTrendingMovies()).thenReturn(moviesWithNoId);
        when(genreService.getAllGenresMap()).thenReturn(getTMDBGenresIdGenreMap());

        List<TMDBMovie> actualTMDBMoviesList = sut.getTrendingMovies();

        assertThat(actualTMDBMoviesList).isEqualTo(expectedTMDBMovieList);
    }

    private List<TMDBMovie> getTMDBMoviesWithoutGenresTitles() throws IOException {
        return mapJsonToObject("tmdb/movies/trending-movies-no-genres-titles.json", new TypeReference<>() {
        });
    }

    private List<TMDBMovie> getTMDBMoviesWithMappedGenresTitles() throws IOException {
        return mapJsonToObject("tmdb/movies/trending-movies-mapped-genres-titles.json", new TypeReference<>() {
        });
    }
}