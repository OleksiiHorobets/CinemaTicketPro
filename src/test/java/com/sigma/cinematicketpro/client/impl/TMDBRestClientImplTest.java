package com.sigma.cinematicketpro.client.impl;

import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import com.sigma.cinematicketpro.dto.tmdb.response.MovieTMDBApiResponse;
import com.sigma.cinematicketpro.dto.tmdb.response.TMDBGenresApiResponse;
import com.sigma.cinematicketpro.entity.tmdb.TMDBGenre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TMDBRestClientImplTest {
    private static final String TRENDING_MOVIES_DAY_URI = "/trending/movie/day";
    private static final String MOVIE_GENRES_URI = "/genre/movie/list?language=en";
    @Mock
    private RestTemplate tmdbRestTemplate;
    @Mock
    private ResponseEntity<MovieTMDBApiResponse> movieResponseEntity;
    @Mock
    private ResponseEntity<TMDBGenresApiResponse> genresResponseEntity;
    @InjectMocks
    private TMDBRestClientImpl sut;

    @Test
    void shouldCallTMDBApiTrendingDayMoviesEndpointWhenGetTrendingMovies() {
        when(tmdbRestTemplate.getForEntity(TRENDING_MOVIES_DAY_URI, MovieTMDBApiResponse.class))
                .thenReturn(movieResponseEntity);

        sut.getTrendingMovies();

        verify(tmdbRestTemplate, times(1))
                .getForEntity(TRENDING_MOVIES_DAY_URI, MovieTMDBApiResponse.class);
        verifyNoMoreInteractions(tmdbRestTemplate);
    }

    @Test
    void shouldReturnEmptyListWhenGetTrendingMoviesAndResponseBodyIsNull() {
        when(tmdbRestTemplate.getForEntity(TRENDING_MOVIES_DAY_URI, MovieTMDBApiResponse.class))
                .thenReturn(movieResponseEntity);
        when(movieResponseEntity.getBody()).thenReturn(null);

        List<TMDBMovie> actual = sut.getTrendingMovies();

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldReturnListOfTMDBMoviesWhenGetTrendingMoviesAndResponseBodyNotNull() {
        RestClientException expectedException = new RestClientException("Connection failed!");

        when(tmdbRestTemplate.getForEntity(TRENDING_MOVIES_DAY_URI, MovieTMDBApiResponse.class))
                .thenThrow(expectedException);

        assertThatThrownBy(() -> sut.getTrendingMovies())
                .isExactlyInstanceOf(RestClientException.class)
                .hasMessage("Connection failed!");
    }

    @Test
    void shouldCallTMDBApiGenresEndpointWhenGetAllGenres() {
        when(tmdbRestTemplate.getForEntity(MOVIE_GENRES_URI, TMDBGenresApiResponse.class))
                .thenReturn(genresResponseEntity);

        sut.getAllGenres();

        verify(tmdbRestTemplate, times(1))
                .getForEntity(MOVIE_GENRES_URI, TMDBGenresApiResponse.class);
        verifyNoMoreInteractions(tmdbRestTemplate);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllGenresAndResponseBodyIsNull() {
        when(tmdbRestTemplate.getForEntity(MOVIE_GENRES_URI, TMDBGenresApiResponse.class))
                .thenReturn(genresResponseEntity);
        when(genresResponseEntity.getBody()).thenReturn(null);

        List<TMDBGenre> actual = sut.getAllGenres();

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldReturnListOfTMDBGenresWhenGetAllGenresAndResponseBodyNotNull() {
        RestClientException expectedException = new RestClientException("Connection failed!");

        when(tmdbRestTemplate.getForEntity(MOVIE_GENRES_URI, TMDBGenresApiResponse.class))
                .thenThrow(expectedException);

        assertThatThrownBy(() -> sut.getAllGenres())
                .isExactlyInstanceOf(RestClientException.class)
                .hasMessage("Connection failed!");
    }
}