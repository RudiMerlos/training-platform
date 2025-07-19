package org.rmc.training_platform.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.rmc.training_platform.annotations.RoleAdmin;
import org.rmc.training_platform.annotations.RoleUser;
import org.rmc.training_platform.dto.EmployeeCourseReadDto;
import org.rmc.training_platform.service.EmployeeCourseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee-courses")
@RequiredArgsConstructor
@Tag(name = "Training", description = "Management of course assignments to employees.")
@RoleAdmin
public class EmployeeCourseController {

    private final EmployeeCourseService employeeCourseService;

    @RoleUser
    @GetMapping("/employee/{employeeId}")
    public List<EmployeeCourseReadDto> getAllCoursesForEmployee(@PathVariable final Long employeeId) {
        return this.employeeCourseService.getCoursesByEmployee(employeeId);
    }

    @GetMapping("/employee/{employeeId}/pending")
    public List<EmployeeCourseReadDto> getPendingCoursesForEmployee(@PathVariable final Long employeeId) {
        return this.employeeCourseService.getPendingCoursesByEmployee(employeeId);
    }

    @PostMapping("/assign")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeCourseReadDto assignCourse(@RequestParam final Long employeeId, @RequestParam final Long courseId) {
        return this.employeeCourseService.assignCourse(employeeId, courseId);
    }

    @PatchMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsCompleted(@PathVariable final Long id) {
        this.employeeCourseService.markAsCompleted(id);
    }

}
