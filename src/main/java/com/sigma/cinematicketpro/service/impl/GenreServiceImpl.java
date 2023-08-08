package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.client.TMDBRestClient;
import com.sigma.cinematicketpro.entity.tmdb.TMDBGenre;
import com.sigma.cinematicketpro.repository.TMDBGenreRepository;
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
    private final TMDBRestClient tmdbRestClient;
    private final TMDBGenreRepository genreRepository;
    private ConcurrentMap<Long, TMDBGenre> idGenreMap;

    @Override
    public Map<Long, TMDBGenre> getAllGenresMap() {
        idGenreMap = Optional.ofNullable(idGenreMap)
                .orElseGet(this::fetchGenresData);

        return idGenreMap;
    }

    private ConcurrentMap<Long, TMDBGenre> fetchGenresData() {
        List<TMDBGenre> genresList = genreRepository.findAll();

        return genresList.isEmpty() ? updateGenresData() : convertGenresListToMap(genresList);
    }

    public ConcurrentMap<Long, TMDBGenre> updateGenresData() {
        List<TMDBGenre> updatedGenres = tmdbRestClient.getAllGenres();

        List<TMDBGenre> savedGenreDoc = genreRepository.saveAll(updatedGenres);

        return convertGenresListToMap(savedGenreDoc);
    }

    private ConcurrentMap<Long, TMDBGenre> convertGenresListToMap(List<TMDBGenre> genreList) {
        return genreList.stream()
                .collect(toConcurrentMap(TMDBGenre::getId, identity()));
    }
}