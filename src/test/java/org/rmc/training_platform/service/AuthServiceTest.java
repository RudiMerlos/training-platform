package org.rmc.training_platform.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rmc.training_platform.domain.User;
import org.rmc.training_platform.domain.enumeration.Role;
import org.rmc.training_platform.dto.UserLoginDto;
import org.rmc.training_platform.dto.UserWriteDto;
import org.rmc.training_platform.exception.DuplicateFieldException;
import org.rmc.training_platform.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private static final String INCORRECT_CREDENTIALS = "Incorrect username or password.";
    private static final String USERNAME_ALREADY_EXISTS = "There is already a user with the username john.";

    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private MessageService messageService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_shouldReturnToken_whenCredentialsAreValid() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("john");
        loginDto.setPassword("secret");

        when(this.authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        when(this.authenticationManager.authenticate(any())).thenReturn(authentication);
        when(this.jwtService.generateToken(authentication)).thenReturn("jwt-token");

        String token = this.authService.authenticate(loginDto);

        assertEquals("jwt-token", token);
        verify(this.authenticationManager).authenticate(any());
        verify(this.jwtService).generateToken(authentication);
    }

    @Test
    void authenticate_shouldThrowBadCredentialsException_whenAuthenticationFails() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("john");
        loginDto.setPassword("wrong");

        when(this.authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        when(this.authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("fail"));
        when(this.messageService.get("user.incorrect.credentials")).thenReturn(INCORRECT_CREDENTIALS);

        assertThatThrownBy(() -> this.authService.authenticate(loginDto)).isInstanceOf(BadCredentialsException.class)
                .hasMessage(INCORRECT_CREDENTIALS);

        verify(this.messageService).get("user.incorrect.credentials");
    }

    @Test
    void register_shouldSaveUser_whenUsernameIsAvailable() {
        UserWriteDto userDto = new UserWriteDto();
        userDto.setUsername("john");
        userDto.setPassword("password");
        userDto.setRoles(Set.of(Role.USER));

        when(this.userService.existsByUsername("john")).thenReturn(false);
        when(this.passwordEncoder.encode("password")).thenReturn("encodedPassword");

        this.authService.register(userDto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(this.userService).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("john", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(userDto.getRoles(), savedUser.getRoles());
    }

    @Test
    void register_shouldThrowException_whenUsernameAlreadyExists() {
        UserWriteDto userDto = new UserWriteDto();
        userDto.setUsername("john");

        when(this.userService.existsByUsername("john")).thenReturn(true);
        when(this.messageService.get("user.name.already.exists", "john")).thenReturn(USERNAME_ALREADY_EXISTS);

        assertThatThrownBy(() -> this.authService.register(userDto)).isInstanceOf(DuplicateFieldException.class)
                .hasMessage(USERNAME_ALREADY_EXISTS);

        verify(this.userService).existsByUsername("john");
    }

    @Test
    void getAllRoles_shouldReturnAllEnumValues() {
        List<Role> roles = authService.getAllRoles();
        assertArrayEquals(Role.values(), roles.toArray());
    }

}
