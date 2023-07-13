package com.sigma.cinematicketpro.repository;

import com.sigma.cinematicketpro.entity.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie, String> {
}