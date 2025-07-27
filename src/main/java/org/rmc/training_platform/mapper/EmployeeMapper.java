package org.rmc.training_platform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.rmc.training_platform.domain.Employee;
import org.rmc.training_platform.dto.EmployeeReadDto;
import org.rmc.training_platform.dto.EmployeeWriteDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeReadDto entityToDto(Employee entity);

    List<EmployeeReadDto> entityToDto(List<Employee> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "employeeCourses", ignore = true)
    Employee dtoToEntity(EmployeeWriteDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "employeeCourses", ignore = true)
    void updateEntityFromDto(EmployeeWriteDto dto, @MappingTarget Employee employee);

}
