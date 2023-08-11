package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.TestUtils;
import com.sigma.cinematicketpro.client.TMDBRestClient;
import com.sigma.cinematicketpro.entity.tmdb.TMDBGenre;
import com.sigma.cinematicketpro.repository.TMDBGenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.sigma.cinematicketpro.TestUtils.getTMDBGenresList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {
    @Mock
    private TMDBRestClient tmdbRestClient;
    @Mock
    private TMDBGenreRepository genreRepository;
    @InjectMocks
    private GenreServiceImpl sut;

    @Test
    void shouldReturnGenresDataFromGenreRepoWhenGetAllGenresMapCalledFirstTime() throws IOException {
        Map<Long, TMDBGenre> expectedGenresMap = TestUtils.getTMDBGenresIdGenreMap();
        when(genreRepository.findAll()).thenReturn(getTMDBGenresList());

        Map<Long, TMDBGenre> actualGenresMap = sut.getAllGenresMap();

        assertThat(actualGenresMap).isEqualTo(expectedGenresMap);

        verify(genreRepository, times(1)).findAll();
        verifyNoInteractions(tmdbRestClient);
    }

    @Test
    void shouldReturnGenresDataFromInMemoryWhenGetAllGenresMapCalledSecondTime() throws IOException {
        Map<Long, TMDBGenre> expectedGenresMap = TestUtils.getTMDBGenresIdGenreMap();
        when(genreRepository.findAll()).thenReturn(getTMDBGenresList());

        sut.getAllGenresMap();

        verify(genreRepository, times(1)).findAll();

        Map<Long, TMDBGenre> actualGenresMap = sut.getAllGenresMap();

        assertThat(actualGenresMap).isEqualTo(expectedGenresMap);

        verifyNoInteractions(tmdbRestClient);
        verifyNoMoreInteractions(genreRepository);
    }

    @Test
    void shouldReturnGenresDataFromRestClientWhenGetAllGenresMapAndNoDataInDB() throws IOException {
        List<TMDBGenre> genresList = getTMDBGenresList();
        Map<Long, TMDBGenre> expectedGenresMap = TestUtils.getTMDBGenresIdGenreMap();
        when(genreRepository.findAll()).thenReturn(Collections.emptyList());
        when(genreRepository.saveAll(any())).thenReturn(genresList);
        when(tmdbRestClient.getAllGenres()).thenReturn(genresList);

        Map<Long, TMDBGenre> actualGenresMap = sut.getAllGenresMap();

        assertThat(actualGenresMap).isEqualTo(expectedGenresMap);

        verify(tmdbRestClient, times(1)).getAllGenres();
        verify(genreRepository, times(1)).findAll();
        verify(genreRepository, times(1)).saveAll(any());
        verifyNoMoreInteractions(tmdbRestClient);
        verifyNoMoreInteractions(genreRepository);
    }
}