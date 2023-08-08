package com.sigma.cinematicketpro.dto.tmdb.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sigma.cinematicketpro.entity.tmdb.TMDBGenre;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TMDBGenresApiResponse {
    List<TMDBGenre> genres;
}