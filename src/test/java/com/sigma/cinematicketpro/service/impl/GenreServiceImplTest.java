package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.TestUtils;
import com.sigma.cinematicketpro.client.TMDBRestClient;
import com.sigma.cinematicketpro.dto.tmdb.TMDBGenre;
import com.sigma.cinematicketpro.entity.GenreDocument;
import com.sigma.cinematicketpro.repository.GenreDocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private GenreDocumentRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl sut;

    @Test
    void shouldReturnGenresDataFromGenreRepoWhenGetAllGenresMapCalledFirstTime() throws IOException {
        GenreDocument genreDocument = new GenreDocument("1", TestUtils.getTMDBGenresList());
        Map<Integer, TMDBGenre> expectedGenresMap = TestUtils.getTMDBGenresIdGenreMap();
        when(genreRepository.findById(anyString())).thenReturn(Optional.of(genreDocument));

        Map<Integer, TMDBGenre> actualGenresMap = sut.getAllGenresMap();

        assertThat(actualGenresMap).isEqualTo(expectedGenresMap);

        verify(genreRepository, times(1)).findById(any());
        verifyNoInteractions(tmdbRestClient);
    }

    @Test
    void shouldReturnGenresDataFromInMemoryWhenGetAllGenresMapCalledSecondTime() throws IOException {
        GenreDocument genreDocument = new GenreDocument("1", TestUtils.getTMDBGenresList());
        Map<Integer, TMDBGenre> expectedGenresMap = TestUtils.getTMDBGenresIdGenreMap();
        when(genreRepository.findById(anyString())).thenReturn(Optional.of(genreDocument));

        sut.getAllGenresMap();

        verify(genreRepository, times(1)).findById(any());

        Map<Integer, TMDBGenre> actualGenresMap = sut.getAllGenresMap();

        assertThat(actualGenresMap).isEqualTo(expectedGenresMap);

        verifyNoInteractions(tmdbRestClient);
        verifyNoMoreInteractions(genreRepository);
    }
    @Test
    void shouldReturnGenresDataFromRestClientWhenGetAllGenresMapAndNoDataInDB() throws IOException {
        List<TMDBGenre> genresList = TestUtils.getTMDBGenresList();
        Map<Integer, TMDBGenre> expectedGenresMap = TestUtils.getTMDBGenresIdGenreMap();
        when(genreRepository.findById(anyString())).thenReturn(Optional.empty());
        when(genreRepository.save(any())).thenReturn(new GenreDocument("1", genresList));
        when(tmdbRestClient.getAllGenres()).thenReturn(genresList);

        Map<Integer, TMDBGenre> actualGenresMap = sut.getAllGenresMap();

        assertThat(actualGenresMap).isEqualTo(expectedGenresMap);

        verify(tmdbRestClient, times(1)).getAllGenres();
        verify(genreRepository, times(1)).findById(any());
        verify(genreRepository, times(1)).save(any());
        verifyNoMoreInteractions(tmdbRestClient);
        verifyNoMoreInteractions(genreRepository);
    }
}