package org.rmc.training_platform.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.rmc.training_platform.dto.CourseReadDto;
import org.rmc.training_platform.dto.CourseWriteDto;
import org.rmc.training_platform.service.CourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Course related operations.")
public class CourseController extends CrudBaseController<CourseWriteDto, CourseReadDto> {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        super(courseService);
        this.courseService = courseService;
    }

    @GetMapping("/by-name")
    public CourseReadDto getByName(@RequestParam String name) {
        return this.courseService.getByName(name);
    }

}
