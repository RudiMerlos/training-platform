package org.rmc.training_platform.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.rmc.training_platform.dto.EmployeeCourseReadDto;
import org.rmc.training_platform.service.EmployeeCourseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-courses")
@RequiredArgsConstructor
@Tag(name = "Training", description = "Management of course assignments to employees.")
public class EmployeeCourseController {

    private final EmployeeCourseService employeeCourseService;

    @GetMapping("/employee/{employeeId}")
    public List<EmployeeCourseReadDto> getAllCoursesForEmployee(@PathVariable Long employeeId) {
        return this.employeeCourseService.getCoursesByEmployee(employeeId);
    }

    @GetMapping("/employee/{employeeId}/pending")
    public List<EmployeeCourseReadDto> getPendingCoursesForEmployee(@PathVariable Long employeeId) {
        return this.employeeCourseService.getPendingCoursesByEmployee(employeeId);
    }

    @PostMapping("/assign")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeCourseReadDto assignCourse(@RequestParam Long employeeId, @RequestParam Long courseId) {
        return this.employeeCourseService.assignCourse(employeeId, courseId);
    }

    @PatchMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsCompleted(@PathVariable Long id) {
        this.employeeCourseService.markAsCompleted(id);
    }

}
