package org.rmc.training_platform.mapper;

import org.mapstruct.Mapper;
import org.rmc.training_platform.domain.EmployeeCourse;
import org.rmc.training_platform.dto.EmployeeCourseReadDto;
import org.rmc.training_platform.dto.EmployeeCourseWriteDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeCourseMapper {

    EmployeeCourseReadDto entityToDto(EmployeeCourse entity);

    List<EmployeeCourseReadDto> entityToDto(List<EmployeeCourse> entities);

    EmployeeCourse dtoToEntity(EmployeeCourseWriteDto dto);

}
