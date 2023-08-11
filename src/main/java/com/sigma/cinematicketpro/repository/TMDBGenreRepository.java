package com.sigma.cinematicketpro.repository;

import com.sigma.cinematicketpro.entity.tmdb.TMDBGenre;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface TMDBGenreRepository extends MongoRepository<TMDBGenre, String> {
}