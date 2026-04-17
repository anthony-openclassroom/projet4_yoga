package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private static final String SECRET =
            "testSecretKeyForTestingPurposesOnly1234567890abcdefghijklmnopqrstuvwxyz";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000);
    }

    // ─── generateJwtToken ───────────────────────────────────────────────────

    @Test
    void generateJwtToken_returnsNonEmptyToken() {
        Authentication auth = buildAuth("test@test.com");

        String token = jwtUtils.generateJwtToken(auth);

        assertThat(token).isNotNull().isNotEmpty();
    }

    // ─── getUserNameFromJwtToken ────────────────────────────────────────────

    @Test
    void getUserNameFromJwtToken_returnsSubject() {
        Authentication auth = buildAuth("test@test.com");
        String token = jwtUtils.generateJwtToken(auth);

        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertThat(username).isEqualTo("test@test.com");
    }

    // ─── validateJwtToken — happy path ─────────────────────────────────────

    @Test
    void validateJwtToken_returnsTrue_forValidToken() {
        String token = jwtUtils.generateJwtToken(buildAuth("test@test.com"));

        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    }

    // ─── validateJwtToken — branches d'erreur ──────────────────────────────

    @Test
    void validateJwtToken_returnsFalse_forMalformedToken() {
        // MalformedJwtException
        assertThat(jwtUtils.validateJwtToken("this.is.not.a.jwt")).isFalse();
    }

    @Test
    void validateJwtToken_returnsFalse_forEmptyString() {
        // IllegalArgumentException
        assertThat(jwtUtils.validateJwtToken("")).isFalse();
    }

    @Test
    void validateJwtToken_returnsFalse_forWrongSignature() {
        // SignatureException — token signé avec un secret différent
        String wrongToken = Jwts.builder()
                .subject("test@test.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000L))
                .signWith(Keys.hmacShaKeyFor(
                        "anotherSecretKeyThatIsCompletelyDifferentFromMainKey12345678".getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThat(jwtUtils.validateJwtToken(wrongToken)).isFalse();
    }

    @Test
    void validateJwtToken_returnsFalse_forExpiredToken() {
        // ExpiredJwtException — token expiré en 1970
        String expiredToken = Jwts.builder()
                .subject("test@test.com")
                .issuedAt(new Date(0))
                .expiration(new Date(1))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThat(jwtUtils.validateJwtToken(expiredToken)).isFalse();
    }

    // ─── helper ─────────────────────────────────────────────────────────────

    private Authentication buildAuth(String email) {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username(email)
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("pass")
                .build();
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
