package org.rmc.training_platform.service;

import lombok.RequiredArgsConstructor;
import org.rmc.training_platform.domain.Employee;
import org.rmc.training_platform.dto.EmployeeReadDto;
import org.rmc.training_platform.dto.EmployeeWriteDto;
import org.rmc.training_platform.exception.ResourceNotFoundException;
import org.rmc.training_platform.mapper.EmployeeMapper;
import org.rmc.training_platform.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final MessageService messageService;

    public EmployeeReadDto create(EmployeeWriteDto employeeWrite) {
        Employee employee = this.employeeRepository.save(this.employeeMapper.dtoToEntity(employeeWrite));
        return this.employeeMapper.entityToDto(employee);
    }

    public EmployeeReadDto update(Long id, EmployeeWriteDto employeeWrite) {
        Employee employee = this.employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(this.messageService.get("employee.not.found")));

        this.employeeMapper.updateEntityFromDto(employeeWrite, employee);
        return this.employeeMapper.entityToDto(this.employeeRepository.save(employee));
    }

    public void delete(Long id) {
        if (!this.employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException(this.messageService.get("employee.not.found"));
        }
        this.employeeRepository.deleteById(id);
    }

}
