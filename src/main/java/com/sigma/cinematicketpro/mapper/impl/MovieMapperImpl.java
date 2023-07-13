package com.sigma.cinematicketpro.mapper.impl;

import com.sigma.cinematicketpro.dto.MovieDTO;
import com.sigma.cinematicketpro.entity.Movie;
import com.sigma.cinematicketpro.mapper.MovieMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

@RequiredArgsConstructor
public class MovieMapperImpl implements MovieMapper {
    private final ModelMapper modelMapper;

    @Override
    public MovieDTO toDto(Movie movie) {
        return modelMapper.map(movie, MovieDTO.class);
    }

    @Override
    public Movie toEntity(MovieDTO movieDto) {
        return modelMapper.map(movieDto, Movie.class);
    }
}