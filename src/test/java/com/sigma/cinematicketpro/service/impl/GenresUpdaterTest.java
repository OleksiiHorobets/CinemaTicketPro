package com.sigma.cinematicketpro.service.impl;

import com.sigma.cinematicketpro.service.GenreService;
import com.sigma.cinematicketpro.service.GenresUpdater;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GenresUpdaterTest {
    @Mock
    private GenreService genreService;
    @InjectMocks
    private GenresUpdater sut;

    @Test
    void shouldCallGenreServiceUpdateGenresDataWhenUpdateGenres() {
        sut.updateGenresData();

        verify(genreService, times(1)).updateGenresData();
        verifyNoMoreInteractions(genreService);
    }
}