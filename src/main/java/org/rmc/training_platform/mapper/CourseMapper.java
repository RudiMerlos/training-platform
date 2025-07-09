package org.rmc.training_platform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.rmc.training_platform.domain.Course;
import org.rmc.training_platform.dto.CourseReadDto;
import org.rmc.training_platform.dto.CourseWriteDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseReadDto entityToDto(Course entity);

    List<CourseReadDto> entityToDto(List<Course> entities);

    Course dtoToEntity(CourseWriteDto dto);

    void updateEntityFromDto(CourseWriteDto dto, @MappingTarget Course course);

}
