package org.rmc.training_platform.service;

import lombok.RequiredArgsConstructor;
import org.rmc.training_platform.dto.EmployeeReadDto;
import org.rmc.training_platform.exception.ResourceNotFoundException;
import org.rmc.training_platform.mapper.EmployeeMapper;
import org.rmc.training_platform.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeQueryService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final MessageService messageService;

    public List<EmployeeReadDto> getAll() {
        return this.employeeMapper.entityToDto(this.employeeRepository.findAll());
    }

    public EmployeeReadDto getById(Long id) {
        return this.employeeRepository.findById(id).map(this.employeeMapper::entityToDto)
                .orElseThrow(() -> new ResourceNotFoundException(this.messageService.get("employee.not.found")));
    }

}
