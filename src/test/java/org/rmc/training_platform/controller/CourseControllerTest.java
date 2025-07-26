package org.rmc.training_platform.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rmc.training_platform.BaseMvcTest;
import org.rmc.training_platform.domain.Course;
import org.rmc.training_platform.repository.CourseRepository;
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
import static org.rmc.training_platform.config.TestConfigConstants.COURSE_FOLDER;
import static org.rmc.training_platform.config.TestConfigConstants.DB_FOLDER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CourseControllerTest extends BaseMvcTest {

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setup(@Autowired final DataSource dataSource) throws SQLException {
        try(final Connection connection = dataSource.getConnection()) {
            deleteAllTables(connection);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(DB_FOLDER + "/init-course-data.sql"));
        }
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCourses_then200() throws Exception {
        this.mvc.perform(get("/api/courses").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(resourceAsString(COURSE_FOLDER + "/get-all-courses.json")));
    }

    @Test
    void getAllCourses_then401() throws Exception {
        this.mvc.perform(get("/api/courses").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCourseById_then200() throws Exception {
        this.mvc.perform(get("/api/courses/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(resourceAsString(COURSE_FOLDER + "/get-course-by-id.json")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCourseById_then404() throws Exception {
        this.mvc.perform(get("/api/courses/4").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(resourceAsString(COURSE_FOLDER +
                        "/get-course-by-id-not-found-exception.json")));
    }

    @Test
    void getCourseById_then401() throws Exception {
        this.mvc.perform(get("/api/courses/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCourseByName_then200() throws Exception {
        this.mvc.perform(get("/api/courses/by-name?name=Course 1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(resourceAsString(COURSE_FOLDER + "/get-course-by-id.json")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCourseByName_then404() throws Exception {
        this.mvc.perform(get("/api/courses/by-name?name=None").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(resourceAsString(COURSE_FOLDER +
                        "/get-course-by-name-not-found-exception.json")));
    }

    @Test
    void getCourseByName_then401() throws Exception {
        this.mvc.perform(get("/api/courses/by-name?name=Course 1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_then201() throws Exception {
        this.mvc.perform(post("/api/courses").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(COURSE_FOLDER + "/post-create-course.json")))
                .andExpect(status().isCreated())
                .andExpect(content().json(resourceAsString(COURSE_FOLDER + "/get-new-course-by-id.json")));

        assertTrue(this.courseRepository.findById(4L).isPresent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createCourse_then403() throws Exception {
        this.mvc.perform(post("/api/courses").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(COURSE_FOLDER + "/post-create-course.json")))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCourse_then401() throws Exception {
        this.mvc.perform(post("/api/courses").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(COURSE_FOLDER + "/post-create-course.json")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourse_then200() throws Exception {
        this.mvc.perform(put("/api/courses/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(COURSE_FOLDER + "/put-update-course-description.json")))
                .andExpect(status().isOk())
                .andExpect(content().json(resourceAsString(COURSE_FOLDER + "/get-updated-course-by-id.json")));

        Optional<Course> course = this.courseRepository.findById(1L);
        assertTrue(course.isPresent());
        assertEquals("New Description", course.get().getDescription());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourse_then409() throws Exception {
        this.mvc.perform(put("/api/courses/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(COURSE_FOLDER + "/put-update-course-name-exists.json")))
                .andExpect(status().isConflict())
                .andExpect(content().json(resourceAsString(COURSE_FOLDER +
                        "/get-updated-course-name-exception.json")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateCourse_then403() throws Exception {
        this.mvc.perform(put("/api/courses/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(COURSE_FOLDER + "/put-update-course-description.json")))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateCourse_then401() throws Exception {
        this.mvc.perform(put("/api/courses/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceAsString(COURSE_FOLDER + "/put-update-course-description.json")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEmloyee_then204() throws Exception {
        this.mvc.perform(delete("/api/courses/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertTrue(this.courseRepository.findById(1L).isEmpty());
        assertEquals(2, this.courseRepository.findAll().size());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteEmloyee_then403() throws Exception {
        this.mvc.perform(delete("/api/courses/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteEmloyee_then401() throws Exception {
        this.mvc.perform(delete("/api/courses/1").accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
