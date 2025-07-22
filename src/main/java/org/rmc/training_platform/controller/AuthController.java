package org.rmc.training_platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rmc.training_platform.annotations.RoleAdmin;
import org.rmc.training_platform.domain.enumeration.Role;
import org.rmc.training_platform.dto.UserLoginDto;
import org.rmc.training_platform.dto.UserWriteDto;
import org.rmc.training_platform.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @RoleAdmin
    @GetMapping("/roles")
    public List<Role> getRoles() {
        return this.authService.getAllRoles();
    }

    @RoleAdmin
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody UserWriteDto userWrite) {
        this.authService.register(userWrite);
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody UserLoginDto userRead) {
        return this.authService.authenticate(userRead);
    }

}
