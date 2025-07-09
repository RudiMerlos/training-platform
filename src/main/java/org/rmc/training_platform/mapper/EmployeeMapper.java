package org.rmc.training_platform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.rmc.training_platform.domain.Employee;
import org.rmc.training_platform.dto.EmployeeReadDto;
import org.rmc.training_platform.dto.EmployeeWriteDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeReadDto entityToDto(Employee entity);

    List<EmployeeReadDto> entityToDto(List<Employee> entities);

    Employee dtoToEntity(EmployeeWriteDto dto);

    void updateEntityFromDto(EmployeeWriteDto dto, @MappingTarget Employee employee);

}
