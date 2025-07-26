package org.rmc.training_platform.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rmc.training_platform.domain.User;
import org.rmc.training_platform.domain.enumeration.Role;
import org.rmc.training_platform.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        // Arrange
        User user = new User();
        user.setUsername("john");
        user.setPassword("secret");

        Role role = Role.USER;
        user.setRoles(Set.of(role));

        when(this.userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = this.userService.loadUserByUsername("john");

        // Assert
        assertEquals("john", userDetails.getUsername());
        assertEquals("secret", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        verify(this.userRepository).findByUsername("john");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
        when(this.userRepository.findByUsername("jane")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> this.userService.loadUserByUsername("jane"));

        verify(this.userRepository).findByUsername("jane");
    }

    @Test
    void existsByUsername_shouldReturnTrue() {
        when(this.userRepository.existsByUsername("john")).thenReturn(true);

        assertTrue(this.userService.existsByUsername("john"));
        verify(this.userRepository).existsByUsername("john");
    }

    @Test
    void existsByUsername_shouldReturnFalse() {
        when(this.userRepository.existsByUsername("jane")).thenReturn(false);

        assertFalse(this.userService.existsByUsername("jane"));
        verify(this.userRepository).existsByUsername("jane");
    }

    @Test
    void save_shouldCallRepositorySave() {
        User user = new User();
        this.userService.save(user);

        verify(this.userRepository).save(user);
    }

}
