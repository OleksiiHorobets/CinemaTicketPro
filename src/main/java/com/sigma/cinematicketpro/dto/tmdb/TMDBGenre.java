package com.sigma.cinematicketpro.dto.tmdb;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.PersistenceCreator;

@Getter
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode
public final class TMDBGenre {
    private final Integer id;
    private final String name;

    public TMDBGenre(int id) {
        this.id = id;
        this.name = null;
    }

    @PersistenceCreator
    public TMDBGenre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}