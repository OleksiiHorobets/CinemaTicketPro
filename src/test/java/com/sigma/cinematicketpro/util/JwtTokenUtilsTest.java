package com.sigma.cinematicketpro.util;

import com.sigma.cinematicketpro.TestUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = JwtTokenUtils.class)
@ActiveProfiles("test")
class JwtTokenUtilsTest {
    @Autowired
    private JwtTokenUtils sut;
    @Value("${spring.jwt.secret}")
    private String secret;
    @Value("${spring.jwt.lifetime}")
    private Duration jwtLifetime;
    private UserDetails userDetails;
    private String validToken;
    private String expiredToken;

    @BeforeEach
    void setUp() {
        userDetails = TestUtils.getUserDetails();
        validToken = generateTestValidToken();
        expiredToken = generateTestExpiredToken();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/auth/tokens/invalid-tokens.csv")
    void shouldThrowJwtExceptionWhenGetUsernameWithInvalidToken(String invalidToken) {
        assertThatThrownBy(() -> sut.getUsername(invalidToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void shouldReturnUsernameWhenGetUsernameValidToken() {
        String accessToken = sut.generateToken(userDetails);

        String subject = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();

        assertThat(subject).isEqualTo(userDetails.getUsername());
    }

    @Test
    void shouldReturnClaimWhenExtractClaim() {
        String subject = sut.extractClaim(validToken, Claims::getSubject);

        assertThat(subject).isEqualTo(userDetails.getUsername());
    }

    @Test
    void shouldReturnTrueWhenTokenIsValid() {
        boolean actualValidationRes = sut.isTokenValid(validToken, userDetails);

        assertThat(actualValidationRes).isTrue();
    }

    @Test
    void shouldThrowExpiredJwtExceptionWhenTokenIsExpired() {
        assertThatThrownBy(() -> sut.isTokenValid(expiredToken, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateTestValidToken() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("additional_data", List.of("additional info", "some new info"));

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtLifetime.toMillis()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .setSubject(userDetails.getUsername())
                .compact();
    }

    private String generateTestExpiredToken() {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiresAt = new Date(System.currentTimeMillis() - 60000);

        return Jwts
                .builder()
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .setSubject(userDetails.getUsername())
                .compact();
    }
}