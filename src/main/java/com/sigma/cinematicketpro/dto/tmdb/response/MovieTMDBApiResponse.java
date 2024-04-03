package com.sigma.cinematicketpro.dto.tmdb.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import java.util.List;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MovieTMDBApiResponse {

  private List<TMDBMovie> results;
}