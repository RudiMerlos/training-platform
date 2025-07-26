package org.rmc.training_platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rmc.training_platform.domain.Course;
import org.rmc.training_platform.domain.Employee;
import org.rmc.training_platform.domain.EmployeeCourse;
import org.rmc.training_platform.domain.enumeration.Status;
import org.rmc.training_platform.dto.EmployeeCourseDto;
import org.rmc.training_platform.exception.IllegalStatusException;
import org.rmc.training_platform.exception.ResourceNotFoundException;
import org.rmc.training_platform.mapper.EmployeeCourseMapper;
import org.rmc.training_platform.repository.CourseRepository;
import org.rmc.training_platform.repository.EmployeeCourseRepository;
import org.rmc.training_platform.repository.EmployeeRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeCourseServiceTest {

    private static final LocalDate NOW = LocalDate.of(2025, 1, 1);

    private static final String EMPLOYEE_ALREADY_ASSIGNED = "The course has already been assigned to this employee.";
    private static final String ASSIGNED_COURSES_AS_COMPLETED = "Only assigned courses can be marked as completed.";
    private static final String EMPLOYEE_NOT_FOUND = "Employee with ID 1 not found.";
    private static final String COURSE_NOT_FOUND = "Course with ID 1 not found.";
    private static final String EMPLOYEE_COURSE_NOT_FOUND = "Employee-course relationship not found.";

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private EmployeeCourseRepository employeeCourseRepository;
    @Mock
    private EmployeeCourseMapper employeeCourseMapper;
    @Mock
    private MessageService messageService;

    @InjectMocks
    private EmployeeCourseService employeeCourseService;

    @BeforeEach
    void setup() {
        final Clock fixedClock = Clock.fixed(NOW.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
        this.employeeCourseService.setClock(fixedClock);
    }

    @Test
    void getCoursesByEmployee_success() {
        Employee employee = new Employee();
        EmployeeCourse ec = new EmployeeCourse();
        EmployeeCourseDto dto = new EmployeeCourseDto();

        when(this.employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(this.employeeCourseRepository.findByEmployee(employee)).thenReturn(List.of(ec));
        when(this.employeeCourseMapper.entityToDto(ec)).thenReturn(dto);

        List<EmployeeCourseDto> result = this.employeeCourseService.getCoursesByEmployee(1L);

        assertEquals(1, result.size());
        verify(this.employeeRepository).findById(1L);
    }

    @Test
    void getPendingCoursesByEmployee_success() {
        Employee employee = new Employee();
        EmployeeCourse ec = new EmployeeCourse();
        EmployeeCourseDto dto = new EmployeeCourseDto();

        when(this.employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(this.employeeCourseRepository.findByEmployeeAndStatus(employee, Status.ASSIGNED)).thenReturn(List.of(ec));
        when(this.employeeCourseMapper.entityToDto(ec)).thenReturn(dto);

        List<EmployeeCourseDto> result = this.employeeCourseService.getPendingCoursesByEmployee(1L);

        assertEquals(1, result.size());
        verify(this.employeeCourseRepository).findByEmployeeAndStatus(employee, Status.ASSIGNED);
    }

    @Test
    void assignCourse_success() {
        Employee employee = new Employee();
        Course course = new Course();
        EmployeeCourse ec = EmployeeCourse.builder().id(10L).employee(employee).course(course).build();
        EmployeeCourseDto dto = new EmployeeCourseDto();

        when(this.employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(this.courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(this.employeeCourseRepository.existsByEmployeeAndCourse(employee, course)).thenReturn(false);
        when(this.employeeCourseRepository.save(any())).thenReturn(ec);
        when(this.employeeCourseMapper.entityToDto(ec)).thenReturn(dto);

        EmployeeCourseDto result = this.employeeCourseService.assignCourse(1L, 2L);

        assertEquals(dto, result);
        verify(this.employeeCourseRepository).save(any());
    }

    @Test
    void assignCourse_alreadyAssigned_throwsException() {
        Employee employee = new Employee();
        Course course = new Course();

        when(this.employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(this.courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(this.employeeCourseRepository.existsByEmployeeAndCourse(employee, course)).thenReturn(true);
        when(this.messageService.get("employee.course.is.already.assigned")).thenReturn(EMPLOYEE_ALREADY_ASSIGNED);

        assertThatThrownBy(() -> this.employeeCourseService.assignCourse(1L, 2L))
                .isInstanceOf(IllegalStatusException.class).hasMessage(EMPLOYEE_ALREADY_ASSIGNED);
    }

    @Test
    void markAsCompleted_setsCompleted() {
        EmployeeCourse ec = new EmployeeCourse();
        Course course = new Course();
        course.setExpirationDays(10L);
        ec.setCourse(course);
        ec.setStatus(Status.ASSIGNED);
        ec.setAssignedOn(LocalDate.of(2024, 12, 25));

        when(this.employeeCourseRepository.findById(1L)).thenReturn(Optional.of(ec));
        when(this.employeeCourseRepository.save(ec)).thenReturn(ec);

        this.employeeCourseService.markAsCompleted(1L);

        assertEquals(Status.COMPLETED, ec.getStatus());
    }

    @Test
    void markAsCompleted_setsExpired() {
        EmployeeCourse ec = new EmployeeCourse();
        Course course = new Course();
        course.setExpirationDays(5L);
        ec.setCourse(course);
        ec.setStatus(Status.ASSIGNED);
        ec.setAssignedOn(LocalDate.of(2024, 12, 20)); // vencido

        when(this.employeeCourseRepository.findById(1L)).thenReturn(Optional.of(ec));
        when(this.employeeCourseRepository.save(ec)).thenReturn(ec);

        this.employeeCourseService.markAsCompleted(1L);

        assertEquals(Status.EXPIRED, ec.getStatus());
    }

    @Test
    void markAsCompleted_invalidStatus_throwsException() {
        EmployeeCourse ec = new EmployeeCourse();
        ec.setStatus(Status.COMPLETED); // no debe permitirse
        when(this.employeeCourseRepository.findById(1L)).thenReturn(Optional.of(ec));
        when(this.messageService.get("employee.course.mark.as.completed")).thenReturn(ASSIGNED_COURSES_AS_COMPLETED);

        assertThatThrownBy(() -> this.employeeCourseService.markAsCompleted(1L))
                .isInstanceOf(IllegalStatusException.class).hasMessage(ASSIGNED_COURSES_AS_COMPLETED);
    }

    @Test
    void getEmployeeById_notFound_throwsException() {
        when(this.employeeRepository.findById(1L)).thenReturn(Optional.empty());
        when(this.messageService.get("employee.not.found", 1L)).thenReturn(EMPLOYEE_NOT_FOUND);

        assertThatThrownBy(() -> this.employeeCourseService.getCoursesByEmployee(1L))
                .isInstanceOf(ResourceNotFoundException.class).hasMessage(EMPLOYEE_NOT_FOUND);
    }

    @Test
    void getCourseById_notFound_throwsException() {
        Employee employee = new Employee();
        when(this.employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(this.courseRepository.findById(1L)).thenReturn(Optional.empty());
        when(this.messageService.get("course.not.found", 1L)).thenReturn(COURSE_NOT_FOUND);

        assertThatThrownBy(() -> this.employeeCourseService.assignCourse(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class).hasMessage(COURSE_NOT_FOUND);
    }

    @Test
    void getEmployeeCourseById_notFound_throwsException() {
        when(this.employeeCourseRepository.findById(1L)).thenReturn(Optional.empty());
        when(this.messageService.get("employee.course.not.found")).thenReturn(EMPLOYEE_COURSE_NOT_FOUND);

        assertThatThrownBy(() -> this.employeeCourseService.markAsCompleted(1L))
                .isInstanceOf(ResourceNotFoundException.class).hasMessage(EMPLOYEE_COURSE_NOT_FOUND);
    }

}
