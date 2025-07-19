package org.rmc.training_platform.service;
import lombok.RequiredArgsConstructor;
import org.rmc.training_platform.domain.Role;
import org.rmc.training_platform.domain.UserApp;
import org.rmc.training_platform.domain.enumeration.RoleType;
import org.rmc.training_platform.dto.UserDto;
import org.rmc.training_platform.exception.DuplicateFieldException;
import org.rmc.training_platform.exception.ResourceNotFoundException;
import org.rmc.training_platform.repository.RoleRepository;
import org.rmc.training_platform.security.jwt.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final MessageService messageService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public String authenticate(final UserDto user) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getUsername(),
                user.getPassword());
        Authentication authResult = this.authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authResult);
        return this.jwtService.generateToken(authResult);
    }

    public void registerUser(final UserDto userDto) {
        this.checkIfUserExists(userDto.getUsername());

        Role roleUser = this.roleRepository.findByRole(RoleType.USER)
                .orElseThrow(() -> new ResourceNotFoundException(this.messageService.get("role.not.found",
                        RoleType.USER.name())));

        this.saveUser(userDto, roleUser);
    }

    public void registerAdmin(final UserDto userDto) {
        this.checkIfUserExists(userDto.getUsername());

        Role roleAdmin = this.roleRepository.findByRole(RoleType.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException(this.messageService.get("role.not.found",
                        RoleType.ADMIN.name())));

        this.saveUser(userDto, roleAdmin);
    }

    private void checkIfUserExists(String username) {
        if (this.userService.exixtsByUsername(username)) {
            throw new DuplicateFieldException(this.messageService.get("user.name.already.exists"));
        }
    }

    private void saveUser(UserDto userDto, Role role) {
        UserApp user = UserApp.builder()
                .username(userDto.getUsername())
                .password(this.passwordEncoder.encode(userDto.getPassword()))
                .roles(Set.of(role))
                .build();
        this.userService.save(user);
    }

}
