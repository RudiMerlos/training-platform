package org.rmc.training_platform.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.rmc.training_platform.domain.Course;
import org.rmc.training_platform.domain.Employee;
import org.rmc.training_platform.domain.EmployeeCourse;
import org.rmc.training_platform.domain.enumeration.Status;
import org.rmc.training_platform.dto.EmployeeCourseReadDto;
import org.rmc.training_platform.exception.ResourceNotFoundException;
import org.rmc.training_platform.mapper.EmployeeCourseMapper;
import org.rmc.training_platform.repository.CourseRepository;
import org.rmc.training_platform.repository.EmployeeCourseRepository;
import org.rmc.training_platform.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeCourseService {

    private final EmployeeRepository employeeRepository;
    private final CourseRepository courseRepository;
    private final EmployeeCourseRepository employeeCourseRepository;
    private final EmployeeCourseMapper employeeCourseMapper;
    private final MessageService messageService;

    @Setter
    private Clock clock;

    @Transactional(readOnly = true)
    public List<EmployeeCourseReadDto> getCoursesByEmployee(Long employeeId) {
        Employee employee = this.getEmployeeById(employeeId);

        return this.employeeCourseRepository.findByEmployee(employee).stream()
                .map(this.employeeCourseMapper::entityToDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeCourseReadDto> getPendingCoursesByEmployee(Long employeeId) {
        Employee employee = this.getEmployeeById(employeeId);

        return this.employeeCourseRepository.findByEmployeeAndStatus(employee, Status.ASSIGNED).stream()
                .map(this.employeeCourseMapper::entityToDto).toList();
    }

    public EmployeeCourseReadDto assignCourse(Long employeeId, Long courseId) {
        Employee employee = this.getEmployeeById(employeeId);
        Course course = this.getCourseById(courseId);

        boolean alreadyAssigned = this.employeeCourseRepository.existsByEmployeeAndCourse(employee, course);
        if (alreadyAssigned) {
            throw new IllegalStateException(this.messageService.get("employee.course.is.already.assigned"));
        }

        EmployeeCourse employeeCourse = EmployeeCourse.builder()
                .employee(employee)
                .course(course)
                .assignedOn(LocalDate.now(this.clock))
                .status(Status.ASSIGNED)
                .build();

        return this.employeeCourseMapper.entityToDto(this.employeeCourseRepository.save(employeeCourse));
    }

    public void markAsCompleted(Long employeeCourseId) {
        EmployeeCourse employeeCourse = this.getEmployeeCourseById(employeeCourseId);

        if (employeeCourse.getStatus() != Status.ASSIGNED) {
            throw new IllegalStateException(this.messageService.get("employee.course.mark.as.completed"));
        }

        LocalDate assignedDate = employeeCourse.getAssignedOn();
        long expirationDays = employeeCourse.getCourse().getExpirationDays();
        LocalDate expirationDate = assignedDate.plusDays(expirationDays);

        if (LocalDate.now(this.clock).isAfter(expirationDate)) {
            employeeCourse.setStatus(Status.EXPIRED);
        } else {
            employeeCourse.setStatus(Status.COMPLETED);
        }

        this.employeeCourseRepository.save(employeeCourse);
    }

    private Employee getEmployeeById(Long employeeId) {
        return this.employeeRepository.findById(employeeId).orElseThrow(() ->
                new ResourceNotFoundException(this.messageService.get("employee.not.found", employeeId)));
    }

    private Course getCourseById(Long courseId) {
        return this.courseRepository.findById(courseId).orElseThrow(() ->
                new ResourceNotFoundException(this.messageService.get("course.not.found", courseId)));
    }

    private EmployeeCourse getEmployeeCourseById(Long employeeCourseId) {
        return this.employeeCourseRepository.findById(employeeCourseId).orElseThrow(() ->
                new ResourceNotFoundException(this.messageService.get("employee.course.not.found", employeeCourseId)));
    }

}
