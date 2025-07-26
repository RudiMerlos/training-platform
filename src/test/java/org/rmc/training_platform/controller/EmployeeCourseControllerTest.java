package org.rmc.training_platform.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rmc.training_platform.BaseMvcTest;
import org.rmc.training_platform.domain.EmployeeCourse;
import org.rmc.training_platform.domain.enumeration.Status;
import org.rmc.training_platform.repository.EmployeeCourseRepository;
import org.rmc.training_platform.service.EmployeeCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.rmc.training_platform.config.TestConfigConstants.COURSE_FOLDER;
import static org.rmc.training_platform.config.TestConfigConstants.DB_FOLDER;
import static org.rmc.training_platform.config.TestConfigConstants.EMPLOYEE_COURSE_FOLDER;
import static org.rmc.training_platform.config.TestConfigConstants.EMPLOYEE_FOLDER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class EmployeeCourseControllerTest extends BaseMvcTest {

    private static final LocalDate NOW = LocalDate.of(2025, 7, 1);

    @Autowired
    private EmployeeCourseService employeeCourseService;

    @Autowired
    private EmployeeCourseRepository employeeCourseRepository;

    @BeforeEach
    void setup(@Autowired final DataSource dataSource) throws SQLException {
        try(final Connection connection = dataSource.getConnection()) {
            deleteAllTables(connection);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(DB_FOLDER + "/init-employee-course-data.sql"));
        }
        final Clock fixedClock = Clock.fixed(NOW.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
        this.employeeCourseService.setClock(fixedClock);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEmployeeCoursesByEmployee_then200() throws Exception {
        this.mvc.perform(get("/api/employee-courses/employee/1").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(resourceAsString(EMPLOYEE_COURSE_FOLDER +
                        "/get-employee-courses-by-employee.json")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEmployeeCoursesByEmployee_then404() throws Exception {
        this.mvc.perform(get("/api/employee-courses/employee/4").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(resourceAsString(EMPLOYEE_FOLDER +
                        "/get-employee-by-id-not-found-exception.json")));
    }

    @Test
    void getEmployeeCoursesByEmployee_then401() throws Exception {
        this.mvc.perform(get("/api/employee-courses/employee/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPendingEmployeeCoursesByEmployee_then200() throws Exception {
        this.mvc.perform(get("/api/employee-courses/employee/1/pending").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(resourceAsString(EMPLOYEE_COURSE_FOLDER +
                        "/get-pending-employee-courses-by-employee.json")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPendingEmployeeCoursesByEmployee_then403() throws Exception {
        this.mvc.perform(get("/api/employee-courses/employee/1/pending").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPendingEmployeeCoursesByEmployee_then404() throws Exception {
        this.mvc.perform(get("/api/employee-courses/employee/4/pending").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(resourceAsString(EMPLOYEE_FOLDER +
                        "/get-employee-by-id-not-found-exception.json")));
    }

    @Test
    void getPendingEmployeeCoursesByEmployee_then401() throws Exception {
        this.mvc.perform(get("/api/employee-courses/employee/1/pending").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignCourse_then201() throws Exception {
        this.mvc.perform(post("/api/employee-courses/assign?employeeId=3&courseId=1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(resourceAsString(EMPLOYEE_COURSE_FOLDER +
                        "/get-employee-course-assigned.json")));

        assertTrue(this.employeeCourseRepository.findById(6L).isPresent());
        assertEquals(6, this.employeeCourseRepository.findAll().size());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignCourseEmployeeNotExists_then404() throws Exception {
        this.mvc.perform(post("/api/employee-courses/assign?employeeId=4&courseId=1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(resourceAsString(EMPLOYEE_FOLDER +
                        "/get-employee-by-id-not-found-exception.json")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignCourseCourseNotExists_then404() throws Exception {
        this.mvc.perform(post("/api/employee-courses/assign?employeeId=3&courseId=4").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(resourceAsString(COURSE_FOLDER +
                        "/get-course-by-id-not-found-exception.json")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignCourseAlreadyExists_then400() throws Exception {
        this.mvc.perform(post("/api/employee-courses/assign?employeeId=1&courseId=1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(resourceAsString(EMPLOYEE_COURSE_FOLDER +
                        "/get-employee-course-already-exists-exception.json")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void assignCourse_then403() throws Exception {
        this.mvc.perform(post("/api/employee-courses/assign?employeeId=3&courseId=1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void assignCourse_then401() throws Exception {
        this.mvc.perform(post("/api/employee-courses/assign?employeeId=3&courseId=1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void markEmployeeCourseAsCompleted_then204() throws Exception {
        this.mvc.perform(patch("/api/employee-courses/1/complete").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<EmployeeCourse> employeeCourse = this.employeeCourseRepository.findById(1L);
        assertTrue(employeeCourse.isPresent());
        assertEquals(Status.COMPLETED, employeeCourse.get().getStatus());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void markEmployeeCourseAsExpired_then204() throws Exception {
        this.mvc.perform(patch("/api/employee-courses/5/complete").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<EmployeeCourse> employeeCourse = this.employeeCourseRepository.findById(5L);
        assertTrue(employeeCourse.isPresent());
        assertEquals(Status.EXPIRED, employeeCourse.get().getStatus());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void markEmployeeCourseAsCompletedNoAssigned_then400() throws Exception {
        this.mvc.perform(patch("/api/employee-courses/3/complete").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(resourceAsString(EMPLOYEE_COURSE_FOLDER +
                        "/get-employee-course-mark-as-completed-no-assigned-exception.json")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void markEmployeeCourseAsCompletedNoExists_then404() throws Exception {
        this.mvc.perform(patch("/api/employee-courses/6/complete").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(resourceAsString(EMPLOYEE_COURSE_FOLDER +
                        "/get-employee-course-mark-as-completed-not-found-exception.json")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void markEmployeeCourseAsCompleted_then403() throws Exception {
        this.mvc.perform(patch("/api/employee-courses/1/complete").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void markEmployeeCourseAsCompleted_then401() throws Exception {
        this.mvc.perform(patch("/api/employee-courses/1/complete").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
