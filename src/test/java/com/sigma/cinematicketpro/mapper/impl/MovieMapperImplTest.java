package com.sigma.cinematicketpro.mapper.impl;

import com.sigma.cinematicketpro.TestUtils;
import com.sigma.cinematicketpro.dto.MovieDTO;
import com.sigma.cinematicketpro.entity.Movie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieMapperImplTest {
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private MovieMapperImpl sut;

    @Test
    void shouldCallModelMapperMapWhenToDto() {
        Movie movie = TestUtils.getValidMovie();
        MovieDTO expectedMovieDTO = TestUtils.getValidMovieDTO();

        when(modelMapper.map(movie, MovieDTO.class)).thenReturn(expectedMovieDTO);

        var actualMovie = sut.toDto(movie);

        assertThat(expectedMovieDTO).isEqualTo(actualMovie);

        verify(modelMapper, times(1)).map(movie, MovieDTO.class);
    }

    @Test
    void shouldCallModelMapperMapWhenToEntity() {
        Movie expectedMovie = TestUtils.getValidMovie();
        MovieDTO movieDto = TestUtils.getValidMovieDTO();

        when(modelMapper.map(movieDto, Movie.class)).thenReturn(expectedMovie);

        var actualMovie = sut.toEntity(movieDto);

        assertThat(expectedMovie).isEqualTo(actualMovie);

        verify(modelMapper, times(1)).map(movieDto, Movie.class);
    }
}