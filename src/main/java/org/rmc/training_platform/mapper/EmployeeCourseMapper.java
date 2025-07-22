package org.rmc.training_platform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.rmc.training_platform.domain.EmployeeCourse;
import org.rmc.training_platform.dto.EmployeeCourseDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeCourseMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "course.id", target = "courseId")
    EmployeeCourseDto entityToDto(EmployeeCourse entity);

    List<EmployeeCourseDto> entityToDto(List<EmployeeCourse> entities);

}
