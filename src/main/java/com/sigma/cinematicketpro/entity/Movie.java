package com.sigma.cinematicketpro.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sigma.cinematicketpro.validation.ValidPosterUri;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "movies")
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

  @Id
  @Size(min = 16, max = 32, message = "Id must be between 16 and 32 characters")
  private String id;

  @NotNull
  @Indexed(unique = true)
  @PositiveOrZero(message = "TMDB ID should not be negative")
  private Long tmdbId;

  @NotBlank(message = "Title should not be blank")
  private String title;

  @Min(0)
  @Max(10)
  private Double voteAverage;

  @NotNull
  @PositiveOrZero
  private Long voteCount;

  @Size(max = 1000)
  private String overview;

  @ValidPosterUri
  private URI posterPath;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate releaseDate;
}