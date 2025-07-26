package org.rmc.training_platform.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rmc.training_platform.BaseMvcTest;
import org.rmc.training_platform.domain.User;
import org.rmc.training_platform.domain.enumeration.Role;
import org.rmc.training_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.rmc.training_platform.config.TestConfigConstants.USER_FOLDER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuthControllerTest extends BaseMvcTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup(@Autowired final DataSource dataSource) throws SQLException {
        try (final Connection connection = dataSource.getConnection()) {
            deleteAllTables(connection);
        }

        this.userRepository.save(User.builder()
                .username("admin")
                .password(this.passwordEncoder.encode("admin123456"))
                .roles(Set.of(Role.ADMIN))
                .build());

        this.userRepository.save(User.builder()
                .username("user")
                .password(this.passwordEncoder.encode("user123456"))
                .roles(Set.of(Role.USER))
                .build());
    }

    @Test
    void login_then200() throws Exception {
        this.mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(USER_FOLDER + "/post-login-user.json")))
                .andExpect(status().isOk());
    }

    @Test
    void login_then401() throws Exception {
        this.mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(USER_FOLDER + "/post-login-user-wrong-password.json")))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(resourceAsString(USER_FOLDER + "/get-incorrect-credentials-exception.json")));
    }

    @Test
    void getRoles_then200() throws Exception {
        String token = this.loginAndGetToken("admin", "admin123456");

        this.mvc.perform(get("/api/auth/roles").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Role.values().length)))
                .andExpect(jsonPath("$", Matchers.containsInAnyOrder(
                        Arrays.stream(Role.values()).map(Enum::name).toArray(String[]::new))));
    }

    @Test
    void getRoles_then403() throws Exception {
        String token = this.loginAndGetToken("user", "user123456");

        this.mvc.perform(get("/api/auth/roles").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().json(resourceAsString(USER_FOLDER + "/get-access-denied-exception.json")));
    }

    @Test
    void register_then201() throws Exception {
        String token = this.loginAndGetToken("admin", "admin123456");

        this.mvc.perform(post("/api/auth/register").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(USER_FOLDER + "/post-register-user.json")))
                .andExpect(status().isCreated());

        assertTrue(this.userRepository.findByUsername("newuser").isPresent());
    }

    @Test
    void register_then403() throws Exception {
        String token = this.loginAndGetToken("user", "user123456");

        this.mvc.perform(post("/api/auth/register").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(USER_FOLDER + "/post-register-user.json")))
                .andExpect(status().isForbidden())
                .andExpect(content().json(resourceAsString(USER_FOLDER + "/get-access-denied-exception.json")));

        assertFalse(this.userRepository.findByUsername("newuser").isPresent());
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String json = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);

        MvcResult result = this.mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isOk()).andReturn();

        return result.getResponse().getContentAsString();
    }

}
