package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.client.TMDBRestClient;
import com.sigma.cinematicketpro.dto.tmdb.TMDBGenre;
import com.sigma.cinematicketpro.entity.GenreDocument;
import com.sigma.cinematicketpro.repository.GenreDocumentRepository;
import com.sigma.cinematicketpro.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toConcurrentMap;


@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private static final String DEFAULT_GENRE_COLLECTION_ID = "1";
    private final TMDBRestClient tmdbRestClient;
    private final GenreDocumentRepository genreRepository;
    private ConcurrentMap<Integer, TMDBGenre> idGenreMap;

    @Override
    public Map<Integer, TMDBGenre> getAllGenresMap() {
        idGenreMap = Optional.ofNullable(idGenreMap)
                .orElseGet(this::fetchGenresData);

        return idGenreMap;
    }

    private ConcurrentMap<Integer, TMDBGenre> fetchGenresData() {
        return genreRepository.findById(DEFAULT_GENRE_COLLECTION_ID)
                .map(GenreDocument::getGenres)
                .map(this::convertGenresListToMap)
                .orElseGet(this::updateGenresData);
    }

    public ConcurrentMap<Integer, TMDBGenre> updateGenresData() {
        List<TMDBGenre> updatedGenres = tmdbRestClient.getAllGenres();

        GenreDocument savedGenreDoc = genreRepository.save(new GenreDocument(DEFAULT_GENRE_COLLECTION_ID, updatedGenres));

        return convertGenresListToMap(savedGenreDoc.getGenres());
    }

    private ConcurrentMap<Integer, TMDBGenre> convertGenresListToMap(List<TMDBGenre> genreList) {
        return genreList.stream()
                .collect(toConcurrentMap(TMDBGenre::getId, identity()));
    }
}