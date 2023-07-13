package com.sigma.cinematicketpro.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;


class UriPosterValidatorTest {
    private final UriPosterValidator sut = new UriPosterValidator();

    @Mock
    private ConstraintValidatorContext context;

    @ParameterizedTest
    @NullSource
    void shouldReturnFalseWhenUriIsNull(URI uri) {
        boolean actualResult = sut.isValid(uri, context);

        assertThat(actualResult).isFalse();
    }

    @ParameterizedTest
    @CsvFileSource(files = "src/test/resources/validation/invalid-uri.csv")
    void shouldReturnFalseWhenUriIsInvalid(URI invalidUri) {
        boolean actualResult = sut.isValid(invalidUri, context);

        assertThat(actualResult).isFalse();
    }

    @ParameterizedTest
    @CsvFileSource(files = "src/test/resources/validation/valid-uri.csv")
    void shouldReturnTrueWhenValidUri(URI validUri) {
        boolean actualResult = sut.isValid(validUri, context);

        assertThat(actualResult).isTrue();
    }
}