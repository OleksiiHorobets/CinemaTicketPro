package com.sigma.cinematicketpro.controller;

import com.sigma.cinematicketpro.dto.MovieDTO;
import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import com.sigma.cinematicketpro.service.MovieService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

  private final MovieService movieService;

  @GetMapping
  public List<MovieDTO> getAllMovies() {
    return movieService.getAllMovies();
  }

  @GetMapping("/{id}")
  public MovieDTO getMovieById(@PathVariable String id) {
    return movieService.getMovieById(id);
  }

  @PostMapping
  public MovieDTO saveMovie(@Valid @RequestBody MovieDTO movieDto) {
    return movieService.saveMovie(movieDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteMovieById(@PathVariable String id) {
    movieService.deleteMovieById(id);
  }

  @PutMapping("/{id}")
  public MovieDTO updateMovie(@PathVariable String id, @Valid @RequestBody MovieDTO movieDto) {
    return movieService.updateMovie(id, movieDto);
  }

  @GetMapping("/trending")
  public List<TMDBMovie> getTrendingMovies() {
    return movieService.getTrendingMovies();
  }
}