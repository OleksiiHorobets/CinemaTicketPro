package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.TestUtils;
import com.sigma.cinematicketpro.client.TMDBRestClient;
import com.sigma.cinematicketpro.dto.MovieDTO;
import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import com.sigma.cinematicketpro.entity.Movie;
import com.sigma.cinematicketpro.exception.ResourceNotFoundException;
import com.sigma.cinematicketpro.mapper.impl.MovieMapperImpl;
import com.sigma.cinematicketpro.repository.MovieRepository;
import com.sigma.cinematicketpro.service.GenreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.sigma.cinematicketpro.TestUtils.getTMDBGenresIdGenreMap;
import static com.sigma.cinematicketpro.TestUtils.getTMDBMoviesWithMappedGenresTitles;
import static com.sigma.cinematicketpro.TestUtils.getTMDBMoviesWithoutGenresTitles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private GenreService genreService;
    @Mock
    private TMDBRestClient tmdbRestClient;
    @Mock
    private MovieMapperImpl movieMapper;
    @InjectMocks
    private MovieServiceImpl sut;

    @Test
    void shouldReturnCallRepositoryFindAllWhenGetAllMovies() {
        sut.getAllMovies();

        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnMovieWhenMovieByGivenIdExists() {
        MovieDTO expectedMovieDTO = TestUtils.getValidMovieDTO();
        Movie movieToReturn = TestUtils.getValidMovie();
        String movieId = expectedMovieDTO.getId();

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movieToReturn));
        when(movieMapper.toDto(any(Movie.class))).thenReturn(expectedMovieDTO);

        MovieDTO actualMovieDTO = sut.getMovieById(movieId);

        assertThat(actualMovieDTO).isEqualTo(expectedMovieDTO);
        verify(movieRepository, times(1)).findById(movieId);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenGetMovieByIdAndGivenIdNotExists() {
        String movieId = "movieId";
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.getMovieById(movieId))
                .isExactlyInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie not found: {%s}".formatted(movieId));

        verify(movieRepository, times(1)).findById(movieId);
    }

    @Test
    void shouldReturnMovieWithGeneratedIdWhenSaveMovieCalled() {
        Movie movieToSave = TestUtils.getValidMovie();
        MovieDTO movieDtoToSave = TestUtils.getValidMovieDtoNoId();
        MovieDTO expectedMovieDTO = TestUtils.getValidMovieDTO();

        when(movieMapper.toEntity(movieDtoToSave)).thenReturn(movieToSave);
        when(movieRepository.save(movieToSave)).thenReturn(movieToSave);
        when(movieMapper.toDto(movieToSave)).thenReturn(expectedMovieDTO);

        MovieDTO actualMovieDTO = sut.saveMovie(movieDtoToSave);

        assertThat(actualMovieDTO).isEqualTo(expectedMovieDTO);
        verify(movieRepository, times(1)).save(movieToSave);
        verify(movieMapper, times(1)).toEntity(any());
        verify(movieMapper, times(1)).toDto(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUpdateAndMovieByIdNotExists() {
        MovieDTO movieDtoToUpdate = TestUtils.getValidMovieDTO();

        String movieId = "movieId";

        when(movieRepository.existsById(movieId)).thenReturn(false);

        assertThatThrownBy(() -> sut.updateMovie(movieId, movieDtoToUpdate))
                .isExactlyInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie not found: {%s}".formatted(movieId));

        verify(movieRepository, times(1)).existsById(movieId);
        verify(movieRepository, times(0)).save(any());
    }

    @Test
    void shouldReturnUpdatedMovieWhenUpdateAndMovieByIdExist() {
        Movie movieToSave = Mockito.mock(Movie.class);
        MovieDTO expectedMovieDto = TestUtils.getValidMovieDTO();
        String movieId = "movieId";

        when(movieRepository.existsById(movieId)).thenReturn(true);
        when(movieRepository.save(movieToSave)).thenReturn(movieToSave);
        when(movieMapper.toEntity(any())).thenReturn(movieToSave);
        when(movieMapper.toDto(any())).thenReturn(expectedMovieDto);

        sut.updateMovie(movieId, MovieDTO.builder().build());

        verify(movieToSave, times(1)).setId(movieId);
        verify(movieRepository, times(1)).save(any());
        verify(movieRepository, times(1)).existsById(any());
        verify(movieMapper, times(1)).toDto(any());
        verify(movieMapper, times(1)).toEntity(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeleteMovieAndMovieByIdNotExists() {
        String movieId = "movieId";

        when(movieRepository.existsById(movieId)).thenReturn(false);

        assertThatThrownBy(() -> sut.deleteMovieById(movieId))
                .isExactlyInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie not found: {%s}".formatted(movieId));

        verify(movieRepository, times(1)).existsById(movieId);
        verify(movieRepository, times(0)).deleteById(movieId);
    }

    @Test
    void shouldCallDeleteOnMovieRepositoryWhenDeleteMovieByIdAndMovieByIdExists() {
        String movieId = "movieId";

        when(movieRepository.existsById(movieId)).thenReturn(true);

        sut.deleteMovieById(movieId);

        verify(movieRepository, times(1)).existsById(movieId);
        verify(movieRepository, times(1)).deleteById(movieId);
    }

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
}