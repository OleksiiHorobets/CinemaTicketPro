package com.sigma.cinematicketpro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sigma.cinematicketpro.dto.MovieDTO;
import com.sigma.cinematicketpro.dto.auth.RegistrationRequest;
import com.sigma.cinematicketpro.dto.tmdb.TMDBGenre;
import com.sigma.cinematicketpro.dto.tmdb.TMDBMovie;
import com.sigma.cinematicketpro.entity.CtpUser;
import com.sigma.cinematicketpro.entity.Movie;
import com.sigma.cinematicketpro.entity.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public final class TestUtils {
    private final static ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private TestUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static List<MovieDTO> getValidMovieDTOs() {
        return List.of(
                MovieDTO.builder()
                        .id("6498a70110b6fe738531da96")
                        .tmdbId(238L)
                        .title("The Godfather")
                        .voteAverage(8.7)
                        .voteCount(18200L)
                        .overview("Spanning the years 1945 to 1955...")
                        .posterPath(toURI("/3bhkrj58Vtu7enYsRolD1fZdja1.jpg"))
                        .releaseDate(LocalDate.parse("1972-03-14", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build(),
                MovieDTO.builder()
                        .id("649712dfe413022a9f2328c7")
                        .tmdbId(238123L)
                        .title("Fast X")
                        .voteAverage(7.3)
                        .voteCount(1848L)
                        .overview("Over many missions and against...")
                        .posterPath(toURI("/fiVW06jE7z9YnO4trhaMEdclSiC.jpg"))
                        .releaseDate(LocalDate.parse("2023-05-17", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build()
        );
    }

    public static MovieDTO getValidMovieDTO() {
        return MovieDTO.builder()
                .id("649712dfe413022a9f2328c7")
                .tmdbId(238123L)
                .title("Fast X")
                .voteAverage(7.3)
                .voteCount(1848L)
                .overview("Over many missions and against...")
                .posterPath(toURI("/fiVW06jE7z9YnO4trhaMEdclSiC.jpg"))
                .releaseDate(LocalDate.parse("2023-05-17", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build();
    }

    public static MovieDTO getInvalidMovieDTO() {
        return MovieDTO.builder()
                .id("11")
                .tmdbId(-100L)
                .title("")
                .voteAverage(15d)
                .voteCount(-10L)
                .overview("Over many missions and against...")
                .posterPath(toURI("/test-invalid-uri"))
                .releaseDate(LocalDate.parse("2023-05-17", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build();
    }

    public static MovieDTO getValidMovieDtoNoId() {
        return MovieDTO.builder()
                .tmdbId(238123L)
                .title("Fast X")
                .voteAverage(7.3)
                .voteCount(1848L)
                .overview("Over many missions and against...")
                .posterPath(toURI("/fiVW06jE7z9YnO4trhaMEdclSiC.jpg"))
                .releaseDate(LocalDate.parse("2023-05-17", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build();
    }

    public static Movie getValidMovie() {
        return Movie.builder()
                .id("649712dfe413022a9f2328c7")
                .tmdbId(238123L)
                .title("Fast X")
                .voteAverage(7.3)
                .voteCount(1848L)
                .overview("Over many missions and against...")
                .posterPath(toURI("/fiVW06jE7z9YnO4trhaMEdclSiC.jpg"))
                .releaseDate(LocalDate.parse("2023-05-17", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build();
    }

    public static <T> T mapMvcResultIntoObject(MvcResult mvcResult, TypeReference<T> typeReference) throws JsonProcessingException, UnsupportedEncodingException {
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(jsonResponse, typeReference);
    }

    private static URI toURI(String str) {
        try {
            return new URI(str);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static UserDetails getUserDetails() {
        return CtpUser.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john_doe@gmail.com")
                .username("john_doe")
                .password("secure_pass")
                .roles(Set.of(
                        Role.builder()
                                .id(1L)
                                .name("ADMIN")
                                .build()
                ))
                .build();
    }

    public static RegistrationRequest getValidRegistrationRequest() {
        return RegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john_doe@gmail.com")
                .username("john_doe")
                .password("secure_pass")
                .build();
    }

    public static List<TMDBGenre> getTMDBGenresList() throws IOException {
        return mapJsonToObject("tmdb/genres/genres.json", new TypeReference<>() {});
    }

    public static Map<Integer, TMDBGenre> getTMDBGenresIdGenreMap() throws IOException {
        return getTMDBGenresList().stream()
                .collect(toMap(TMDBGenre::getId, identity()));
    }

    public static List<TMDBMovie> getTMDBMoviesWithMappedGenresTitles() throws IOException {
        return mapJsonToObject("tmdb/movies/trending-movies-mapped-genres-titles.json", new TypeReference<>() {});
    }

    public static List<TMDBMovie> getTMDBMoviesWithoutGenresTitles() throws IOException {
        return mapJsonToObject("tmdb/movies/trending-movies-no-genres-titles.json", new TypeReference<>() {
        });
    }

    public static <T> T mapJsonToObject(String resourcePath, TypeReference<T> typeReference) throws IOException {
        var resource = TestUtils.class.getClassLoader().getResourceAsStream(resourcePath);
        return objectMapper.readValue(resource, typeReference);
    }
}