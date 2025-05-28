package pl.sgorski.AirLink.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTests {

    private final JwtService jwtService = new JwtService();

    private Field expirationField;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        Field secretKeyField = JwtService.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtService, "6F5A743677397A24432646294A404E635266556A586E3272357538782F413F44");

        expirationField = JwtService.class.getDeclaredField("jwtExpiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, 1000 * 60 * 60);
    }

    @Test
    void shouldGenerateValidToken() {
        when(userDetails.getUsername()).thenReturn("email@email.com");
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals("email@email.com", jwtService.extractUsername(token));
    }

    @Test
    void shouldValidateCorrectToken() {
        when(userDetails.getUsername()).thenReturn("email@email.com");
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldInvalidateTokenIfUsernameMismatch() {
        when(userDetails.getUsername()).thenReturn("email@email.com");
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUser = mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("wrong@email.com");

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void shouldExtractClaim() {
        when(userDetails.getUsername()).thenReturn("email@email.com");
        String token = jwtService.generateToken(userDetails);

        String subject = jwtService.extractClaim(token, Claims::getSubject);
        assertEquals("email@email.com", subject);
    }

    @Test
    void shouldGenerateTokenWithExtraClaims() {
        when(userDetails.getUsername()).thenReturn("email@email.com");
        Map<String, Object> claims = Map.of("role", "ADMIN");
        String token = jwtService.generateToken(claims, userDetails);

        String role = jwtService.extractClaim(token, c -> (String) c.get("role"));
        assertEquals("ADMIN", role);
    }

    @Test
    void shouldReturnFalseForExpiredToken() throws Exception {
        when(userDetails.getUsername()).thenReturn("email@email.com");
        expirationField.set(jwtService, 10);
        String token = jwtService.generateToken(userDetails);
        Thread.sleep(50);

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldThrowWhenTokenMalformed() {
        String invalidToken = "not.a.valid.token";

        assertThrows(Exception.class, () -> jwtService.extractUsername(invalidToken));
    }
}
