package com.sigma.cinematicketpro.entity;

import com.sigma.cinematicketpro.dto.tmdb.TMDBGenre;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movie_genres")
public class GenreDocument {
    @Id
    private String id;
    @NotNull
    private List<TMDBGenre> genres;
}