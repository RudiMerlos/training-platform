package org.rmc.training_platform.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rmc.training_platform.domain.Employee;
import org.rmc.training_platform.dto.EmployeeReadDto;
import org.rmc.training_platform.dto.EmployeeWriteDto;
import org.rmc.training_platform.exception.DuplicateFieldException;
import org.rmc.training_platform.exception.ResourceNotFoundException;
import org.rmc.training_platform.mapper.EmployeeMapper;
import org.rmc.training_platform.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    private static final String EMPLOYEE_NOT_FOUND = "Employee with ID 1 not found.";
    private static final String EMPLOYEE_EMAIL_NOT_FOUND = "Employee with email john@example.com not found.";
    private static final String EMAIL_ALREADY_EXISTS = "There is already an employee with the email john@example.com.";

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getAll_shouldReturnEmployeeList() {
        Employee employee = new Employee();
        EmployeeReadDto employeeDto = new EmployeeReadDto();

        when(this.employeeRepository.findAll()).thenReturn(List.of(employee));
        when(this.employeeMapper.entityToDto(List.of(employee))).thenReturn(List.of(employeeDto));

        List<EmployeeReadDto> result = this.employeeService.getAll();

        assertThat(result).hasSize(1);
        verify(this.employeeRepository).findAll();
        verify(this.employeeMapper).entityToDto(List.of(employee));
    }

    @Test
    void getById_shouldReturnEmployee_whenFound() {
        Long id = 1L;
        Employee employee = new Employee();
        EmployeeReadDto employeeDto = new EmployeeReadDto();

        when(this.employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(this.employeeMapper.entityToDto(employee)).thenReturn(employeeDto);

        EmployeeReadDto result = this.employeeService.getById(id);

        assertThat(result).isEqualTo(employeeDto);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        Long id = 1L;

        when(this.employeeRepository.findById(id)).thenReturn(Optional.empty());
        when(this.messageService.get("employee.not.found", id)).thenReturn(EMPLOYEE_NOT_FOUND);

        assertThatThrownBy(() -> this.employeeService.getById(id)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(EMPLOYEE_NOT_FOUND);
    }

    @Test
    void getByEmail_shouldReturnEmployee_whenFound() {
        String email = "john@example.com";
        Employee employee = new Employee();
        EmployeeReadDto employeeDto = new EmployeeReadDto();

        when(this.employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));
        when(this.employeeMapper.entityToDto(employee)).thenReturn(employeeDto);

        EmployeeReadDto result = this.employeeService.getByEmail(email);

        assertThat(result).isEqualTo(employeeDto);
    }

    @Test
    void getByEmail_shouldThrow_whenNotFound() {
        String email = "john@example.com";

        when(this.employeeRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(this.messageService.get("employee.email.not.found", email)).thenReturn(EMPLOYEE_EMAIL_NOT_FOUND);

        assertThatThrownBy(() -> this.employeeService.getByEmail(email)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(EMPLOYEE_EMAIL_NOT_FOUND);
    }

    @Test
    void create_shouldSaveNewEmployee() {
        EmployeeWriteDto employeeDto = EmployeeWriteDto.builder().name("John").email("john@example.com")
                .department("1A").build();

        Employee employee = new Employee();
        EmployeeReadDto resultDto = new EmployeeReadDto();

        when(this.employeeRepository.existsByEmail(employeeDto.getEmail())).thenReturn(false);
        when(this.employeeMapper.dtoToEntity(employeeDto)).thenReturn(employee);
        when(this.employeeRepository.save(employee)).thenReturn(employee);
        when(this.employeeMapper.entityToDto(employee)).thenReturn(resultDto);

        EmployeeReadDto result = this.employeeService.create(employeeDto);

        assertThat(result).isEqualTo(resultDto);
    }

    @Test
    void create_shouldThrow_whenEmailExists() {
        EmployeeWriteDto employeeDto = EmployeeWriteDto.builder().name("John").email("john@example.com")
                .department("1A").build();

        when(this.employeeRepository.existsByEmail(employeeDto.getEmail())).thenReturn(true);
        when(this.messageService.get("employee.email.already.exists", employeeDto.getEmail()))
                .thenReturn(EMAIL_ALREADY_EXISTS);

        assertThatThrownBy(() -> this.employeeService.create(employeeDto)).isInstanceOf(DuplicateFieldException.class)
                .hasMessage(EMAIL_ALREADY_EXISTS);
    }

    @Test
    void update_shouldModifyEmployee_whenValid() {
        Long id = 1L;
        String newEmail = "new@example.com";

        EmployeeWriteDto writeDto = new EmployeeWriteDto();
        writeDto.setEmail(newEmail);

        Employee existingEmployee = new Employee();
        existingEmployee.setId(id);
        existingEmployee.setEmail("old@example.com");

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(id);
        updatedEmployee.setEmail(newEmail);

        EmployeeReadDto expectedDto = new EmployeeReadDto();
        expectedDto.setEmail(newEmail);

        when(this.employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(this.employeeRepository.existsByEmail(newEmail)).thenReturn(false);
        doNothing().when(this.employeeMapper).updateEntityFromDto(writeDto, existingEmployee);
        when(this.employeeRepository.save(existingEmployee)).thenReturn(updatedEmployee);
        when(this.employeeMapper.entityToDto(updatedEmployee)).thenReturn(expectedDto);

        EmployeeReadDto result = this.employeeService.update(id, writeDto);

        assertThat(result.getEmail()).isEqualTo(newEmail);

        verify(this.employeeRepository).findById(id);
        verify(this.employeeRepository).existsByEmail(newEmail);
        verify(this.employeeMapper).updateEntityFromDto(writeDto, existingEmployee);
        verify(this.employeeRepository).save(existingEmployee);
        verify(this.employeeMapper).entityToDto(updatedEmployee);
    }

    @Test
    void delete_shouldCallRepositoryDelete_whenExists() {
        Long id = 1L;

        when(this.employeeRepository.existsById(id)).thenReturn(true);

        this.employeeService.delete(id);

        verify(this.employeeRepository).deleteById(id);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        Long id = 1L;

        when(this.employeeRepository.existsById(id)).thenReturn(false);
        when(this.messageService.get("employee.not.found", id)).thenReturn(EMPLOYEE_NOT_FOUND);

        assertThatThrownBy(() -> this.employeeService.delete(id)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(EMPLOYEE_NOT_FOUND);

    }

}
