package org.rmc.training_platform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.rmc.training_platform.domain.Course;
import org.rmc.training_platform.dto.CourseReadDto;
import org.rmc.training_platform.dto.CourseWriteDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseReadDto entityToDto(Course entity);

    List<CourseReadDto> entityToDto(List<Course> entities);

    @Mapping(target = "employeeCourses", ignore = true)
    Course dtoToEntity(CourseWriteDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "employeeCourses", ignore = true)
    void updateEntityFromDto(CourseWriteDto dto, @MappingTarget Course course);

}
