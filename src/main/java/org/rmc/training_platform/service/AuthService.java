package org.rmc.training_platform.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final MessageService messageService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public String authenticate(final UserLoginDto user) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getUsername(),
                user.getPassword());

        Authentication authResult;
        try {
            authResult = this.authenticationManagerBuilder.getObject().authenticate(authToken);
        } catch(Exception ex) {
            throw new BadCredentialsException(this.messageService.get("user.incorrect.credentials"));
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);
        return this.jwtService.generateToken(authResult);
    }

    public void register(final UserWriteDto user) {
        //this.checkAllFieldsRequired(user);

        this.checkIfUserExists(user.getUsername());

        this.saveUser(user);
    }

    public List<Role> getAllRoles() {
        return List.of(Role.values());
    }

    private void checkIfUserExists(String username) {
        if (this.userService.existsByUsername(username)) {
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
