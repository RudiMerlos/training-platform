package org.rmc.training_platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rmc.training_platform.security.jwt.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "my-super-secret-key-which-is-very-long-to-work";
    private static final long EXPIRATION = 1000 * 60 * 60; // 1h

    @BeforeEach
    void setUp() throws Exception {
        this.jwtService = new JwtService();

        Field secretField = JwtService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(this.jwtService, SECRET);

        Field expirationField = JwtService.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(this.jwtService, EXPIRATION);
    }

    @Test
    void generateToken_and_validate_shouldWorkCorrectly() {
        User user = new User("john", "pass", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        String token = this.jwtService.generateToken(authentication);

        assertNotNull(token);
        String username = this.jwtService.extractUsername(token);
        assertEquals("john", username);
        assertTrue(this.jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenUsernameDoesNotMatch() {
        User user = new User("alice", "pass", Set.of());
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        String token = this.jwtService.generateToken(authentication);
        User otherUser = new User("bob", "pass", Set.of());

        assertFalse(this.jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenExpired() throws Exception {
        Field expirationField = JwtService.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(this.jwtService, -1000L); // token expirado

        User user = new User("john", "pass", Set.of());
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        String token = this.jwtService.generateToken(authentication);

        assertFalse(this.jwtService.isTokenValid(token, user));
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        User user = new User("john", "pass", Set.of());
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        String token = this.jwtService.generateToken(authentication);
        String username = this.jwtService.extractUsername(token);

        assertEquals("john", username);
    }

}
