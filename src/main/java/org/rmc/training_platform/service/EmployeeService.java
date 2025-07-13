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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService implements CrudBaseService<EmployeeWriteDto, EmployeeReadDto> {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final MessageService messageService;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeReadDto> getAll() {
        return this.employeeMapper.entityToDto(this.employeeRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeReadDto getById(Long id) {
        Employee employee = this.employeeRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(this.messageService.get("employee.not.found", id)));
        return this.employeeMapper.entityToDto(employee);
    }

    @Override
    public EmployeeReadDto create(EmployeeWriteDto employeeWrite) {
        Employee employee = this.employeeRepository.save(this.employeeMapper.dtoToEntity(employeeWrite));
        return this.employeeMapper.entityToDto(employee);
    }

    @Override
    public EmployeeReadDto update(Long id, EmployeeWriteDto employeeWrite) {
        Employee employee = this.getEmployeeById(id);

        this.employeeMapper.updateEntityFromDto(employeeWrite, employee);
        return this.employeeMapper.entityToDto(this.employeeRepository.save(employee));
    }

    @Override
    public void delete(Long id) {
        if (!this.employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException(this.messageService.get("employee.not.found"));
        }
        this.employeeRepository.deleteById(id);
    }

    private Employee getEmployeeById(Long employeeId) throws ResourceNotFoundException {
        return this.employeeRepository.findById(employeeId).orElseThrow(() ->
                new ResourceNotFoundException(this.messageService.get("employee.not.found", employeeId)));
    }

    @Transactional(readOnly = true)
    public EmployeeReadDto getByEmail(String email) {
        return this.employeeRepository.findByEmail(email).map(this.employeeMapper::entityToDto).orElseThrow(()->
                new ResourceNotFoundException(this.messageService.get("employee.email.not.found", email)));
    }

}
