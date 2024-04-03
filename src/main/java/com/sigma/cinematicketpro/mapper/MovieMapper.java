package com.sigma.cinematicketpro.mapper;

import com.sigma.cinematicketpro.dto.MovieDTO;
import com.sigma.cinematicketpro.entity.Movie;

public interface MovieMapper {

  MovieDTO toDto(Movie movie);

  Movie toEntity(MovieDTO movieDto);
}