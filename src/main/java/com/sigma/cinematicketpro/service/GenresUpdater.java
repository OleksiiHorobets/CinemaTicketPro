package com.sigma.cinematicketpro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenresUpdater {
    private final GenreService genreService;

    @Scheduled(cron = "@weekly")
    @CacheEvict(value = "movieGenresMap", allEntries = true)
    public void updateGenresData() {
        genreService.updateGenresData();
        log.info("Updating movies genres data");
    }
}