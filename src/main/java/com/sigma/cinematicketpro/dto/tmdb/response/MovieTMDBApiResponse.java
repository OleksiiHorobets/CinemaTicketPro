package com.sigma.cinematicketpro.dto.tmdb.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MovieTMDBApiResponse {
    private List<TMDBMovie> results;
}