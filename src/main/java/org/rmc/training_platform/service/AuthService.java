package org.rmc.training_platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rmc.training_platform.domain.User;
import org.rmc.training_platform.domain.enumeration.Role;
import org.rmc.training_platform.dto.UserLoginDto;
import org.rmc.training_platform.dto.UserWriteDto;
import org.rmc.training_platform.exception.DuplicateFieldException;
import org.rmc.training_platform.security.jwt.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final MessageService messageService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public String authenticate(final UserLoginDto user) {
        LOGGER.info("Login user with username: {}", user.getUsername());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getUsername(),
                user.getPassword());

        Authentication authResult;
        try {
            authResult = this.authenticationManagerBuilder.getObject().authenticate(authToken);
        } catch(Exception ex) {
            LOGGER.error("Username or password incorrect");
            throw new BadCredentialsException(this.messageService.get("user.incorrect.credentials"));
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);
        LOGGER.debug("User successfully loged in");
        return this.jwtService.generateToken(authResult);
    }

    public void register(final UserWriteDto user) {
        LOGGER.info("Registering user with username: {}, and roles {}", user.getUsername(), user.getRoles());
        this.checkIfUserExists(user.getUsername());

        this.saveUser(user);
        LOGGER.debug("User {} registered successfully", user.getUsername());
    }

    public List<Role> getAllRoles() {
        return List.of(Role.values());
    }

    private void checkIfUserExists(String username) {
        if (this.userService.existsByUsername(username)) {
            LOGGER.error("Username {} already exists", username);
            throw new DuplicateFieldException(this.messageService.get("user.name.already.exists", username));
        }
    }

    private void saveUser(UserWriteDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .password(this.passwordEncoder.encode(userDto.getPassword()))
                .roles(userDto.getRoles())
                .build();
        this.userService.save(user);
    }

}
