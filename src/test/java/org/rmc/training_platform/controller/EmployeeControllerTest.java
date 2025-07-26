package org.rmc.training_platform.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rmc.training_platform.BaseMvcTest;
import org.rmc.training_platform.domain.Employee;
import org.rmc.training_platform.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.rmc.training_platform.config.TestConfigConstants.DB_FOLDER;
import static org.rmc.training_platform.config.TestConfigConstants.EMPLOYEE_FOLDER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class EmployeeControllerTest extends BaseMvcTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setup(@Autowired final DataSource dataSource) throws SQLException {
        try (final Connection connection = dataSource.getConnection()) {
            deleteAllTables(connection);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(DB_FOLDER + "/init-employee-data.sql"));
        }
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllEmployees_then200() throws Exception {
        this.mvc.perform(get("/api/employees").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(resourceAsString(EMPLOYEE_FOLDER + "/get-all-employees.json")));
    }

    @Test
    void getAllEmployees_then401() throws Exception {
        this.mvc.perform(get("/api/employees").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEmployeeById_then200() throws Exception {
        this.mvc.perform(get("/api/employees/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(resourceAsString(EMPLOYEE_FOLDER + "/get-employee-by-id.json")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEmployeeById_then404() throws Exception {
        this.mvc.perform(get("/api/employees/4").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(resourceAsString(EMPLOYEE_FOLDER +
                        "/get-employee-by-id-not-found-exception.json")));
    }

    @Test
    void getEmployeeById_then401() throws Exception {
        this.mvc.perform(get("/api/employees/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEmployeeByEmail_then200() throws Exception {
        this.mvc.perform(get("/api/employees/by-email?email=employee1@example.com").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(resourceAsString(EMPLOYEE_FOLDER + "/get-employee-by-id.json")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEmployeeByEmail_then404() throws Exception {
        this.mvc.perform(get("/api/employees/by-email?email=none@email.com").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(resourceAsString(EMPLOYEE_FOLDER +
                        "/get-employee-by-email-not-found-exception.json")));
    }

    @Test
    void getEmployeeByEmail_then401() throws Exception {
        this.mvc.perform(get("/api/employees/by-email?email=employee1@example.com").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createEmployee_then201() throws Exception {
        this.mvc.perform(post("/api/employees").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(EMPLOYEE_FOLDER + "/post-create-employee.json")))
                .andExpect(status().isCreated())
                .andExpect(content().json(resourceAsString(EMPLOYEE_FOLDER + "/get-new-employee-by-id.json")));

        assertTrue(this.employeeRepository.findById(4L).isPresent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createEmployee_then403() throws Exception {
        this.mvc.perform(post("/api/employees").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(EMPLOYEE_FOLDER + "/post-create-employee.json")))
                .andExpect(status().isForbidden());
    }

    @Test
    void createEmployee_then401() throws Exception {
        this.mvc.perform(post("/api/employees").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(EMPLOYEE_FOLDER + "/post-create-employee.json")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateEmployee_then200() throws Exception {
        this.mvc.perform(put("/api/employees/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(EMPLOYEE_FOLDER + "/put-update-employee-department.json")))
                .andExpect(status().isOk())
                .andExpect(content().json(resourceAsString(EMPLOYEE_FOLDER + "/get-updated-employee-by-id.json")));

        Optional<Employee> employee = this.employeeRepository.findById(1L);
        assertTrue(employee.isPresent());
        assertEquals("Depart 2", employee.get().getDepartment());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateEmployee_then409() throws Exception {
        this.mvc.perform(put("/api/employees/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(EMPLOYEE_FOLDER + "/put-update-employee-email-exists.json")))
                .andExpect(status().isConflict())
                .andExpect(content().json(resourceAsString(EMPLOYEE_FOLDER +
                        "/get-updated-employee-email-exception.json")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateEmployee_then403() throws Exception {
        this.mvc.perform(put("/api/employees/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(EMPLOYEE_FOLDER + "/put-update-employee-department.json")))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateEmployee_then401() throws Exception {
        this.mvc.perform(put("/api/employees/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(EMPLOYEE_FOLDER + "/put-update-employee-department.json")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEmloyee_then204() throws Exception {
        this.mvc.perform(delete("/api/employees/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertTrue(this.employeeRepository.findById(1L).isEmpty());
        assertEquals(2, this.employeeRepository.findAll().size());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteEmloyee_then403() throws Exception {
        this.mvc.perform(delete("/api/employees/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteEmloyee_then401() throws Exception {
        this.mvc.perform(delete("/api/employees/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
