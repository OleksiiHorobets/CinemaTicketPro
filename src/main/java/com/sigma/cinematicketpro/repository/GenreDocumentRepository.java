package com.sigma.cinematicketpro.repository;

import com.sigma.cinematicketpro.entity.GenreDocument;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface GenreDocumentRepository extends MongoRepository<GenreDocument, String> {

}