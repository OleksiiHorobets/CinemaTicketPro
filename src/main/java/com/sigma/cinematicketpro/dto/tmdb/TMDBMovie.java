package com.sigma.cinematicketpro.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sigma.cinematicketpro.entity.tmdb.TMDBGenre;
import java.net.URI;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TMDBMovie {

  @JsonAlias({"id"})
  private Long tmdbId;

  @JsonAlias("adult")
  private boolean adult;

  @JsonAlias({"backdrop_path"})
  private URI backdropPath;

  @JsonAlias({"genre_ids", "genres"})
  private Set<TMDBGenre> genres;

  @JsonAlias("original_language")
  private String originalLanguage;

  @JsonAlias("original_title")
  private String originalTitle;

  @JsonAlias("overview")
  private String overview;

  @JsonAlias("popularity")
  private Double popularity;

  @JsonAlias("poster_path")
  private URI posterPath;

  @JsonAlias("release_date")
  private LocalDate releaseDate;

  @JsonAlias("title")
  private String title;

  @JsonAlias("vote_average")
  private Double voteAverage;

  @JsonAlias("vote_count")
  private Long voteCount;
}