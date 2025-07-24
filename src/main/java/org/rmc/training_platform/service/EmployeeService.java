package org.rmc.training_platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rmc.training_platform.domain.Employee;
import org.rmc.training_platform.dto.EmployeeReadDto;
import org.rmc.training_platform.dto.EmployeeWriteDto;
import org.rmc.training_platform.exception.DuplicateFieldException;
import org.rmc.training_platform.exception.ResourceNotFoundException;
import org.rmc.training_platform.mapper.EmployeeMapper;
import org.rmc.training_platform.repository.EmployeeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService implements CrudBaseService<EmployeeWriteDto, EmployeeReadDto> {

    private static final String EMPLOYEES = "emloyees.";

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final MessageService messageService;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(EMPLOYEES + "getAll")
    public List<EmployeeReadDto> getAll() {
        return this.employeeMapper.entityToDto(this.employeeRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = EMPLOYEES + "getById", key = "#id")
    public EmployeeReadDto getById(Long id) {
        return this.employeeMapper.entityToDto(this.getEmployeeById(id));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = EMPLOYEES + "getAll", allEntries = true),
            @CacheEvict(cacheNames = EMPLOYEES + "getById", key = "#result.id", condition = "#result != null"),
            @CacheEvict(cacheNames = EMPLOYEES + "getByEmail", key = "#result.email", condition = "#result != null")
    })
    public EmployeeReadDto create(EmployeeWriteDto employeeWrite) {
        LOGGER.info("Creating employee with email: {}", employeeWrite.getEmail());
        this.checkIfExistsEmail(employeeWrite.getEmail());
        Employee employee = this.employeeRepository.save(this.employeeMapper.dtoToEntity(employeeWrite));
        LOGGER.debug("Employee created with ID: {}", employee.getId());
        return this.employeeMapper.entityToDto(employee);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = EMPLOYEES + "getAll", allEntries = true),
            @CacheEvict(cacheNames = EMPLOYEES + "getById", key = "#id"),
            @CacheEvict(cacheNames = EMPLOYEES + "getByEmail", allEntries = true)
    })
    public EmployeeReadDto update(Long id, EmployeeWriteDto employeeWrite) {
        LOGGER.info("Updating employee with ID: {}", id);
        Employee employee = this.getEmployeeById(id);

        String email = employeeWrite.getEmail();
        if (!email.isBlank() && !employee.getEmail().equals(email)) {
            this.checkIfExistsEmail(email);
        }

        this.employeeMapper.updateEntityFromDto(employeeWrite, employee);
        LOGGER.debug("Employee with ID {} updated", id);
        return this.employeeMapper.entityToDto(this.employeeRepository.save(employee));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = EMPLOYEES + "getAll", allEntries = true),
            @CacheEvict(cacheNames = EMPLOYEES + "getById", key = "#id"),
            @CacheEvict(cacheNames = EMPLOYEES + "getByEmail", allEntries = true)
    })
    public void delete(Long id) {
        LOGGER.info("Deleting employee with ID: {}", id);
        if (!this.employeeRepository.existsById(id)) {
            LOGGER.error("Employee with ID {} not found", id);
            throw new ResourceNotFoundException(this.messageService.get("employee.not.found", id));
        }
        this.employeeRepository.deleteById(id);
        LOGGER.debug("Employee with ID {} deleted", id);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = EMPLOYEES + "getByEmail", key = "#email")
    public EmployeeReadDto getByEmail(String email) {
        return this.employeeRepository.findByEmail(email).map(this.employeeMapper::entityToDto).orElseThrow(() -> {
            LOGGER.error("Employee with email {} not exists", email);
            return new ResourceNotFoundException(this.messageService.get("employee.email.not.found", email));
        });
    }

    private Employee getEmployeeById(Long employeeId) throws ResourceNotFoundException {
        return this.employeeRepository.findById(employeeId).orElseThrow(() -> {
            LOGGER.error("Employee with ID {} not exists", employeeId);
            return new ResourceNotFoundException(this.messageService.get("employee.not.found", employeeId));
        });
    }

    private void checkIfExistsEmail(String email) {
        if (this.employeeRepository.existsByEmail(email)) {
            LOGGER.error("Employee with email {} already exists", email);
            throw new DuplicateFieldException(this.messageService.get("employee.email.already.exists", email));
        }
    }

}
