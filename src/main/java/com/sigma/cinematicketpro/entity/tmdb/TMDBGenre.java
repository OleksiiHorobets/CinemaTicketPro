package com.sigma.cinematicketpro.entity.tmdb;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "movie_genres")
@Getter
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode
public final class TMDBGenre {

  @Id
  private final Long id;
  private final String name;

  public TMDBGenre(long id) {
    this.id = id;
    this.name = null;
  }

  @PersistenceCreator
  public TMDBGenre(Long id, String name) {
    this.id = id;
    this.name = name;
  }
}