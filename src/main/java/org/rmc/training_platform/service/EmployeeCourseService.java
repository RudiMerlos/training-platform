package org.rmc.training_platform.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeCourseService {

    private final EmployeeRepository employeeRepository;
    private final CourseRepository courseRepository;
    private final EmployeeCourseRepository employeeCourseRepository;
    private final EmployeeCourseMapper employeeCourseMapper;
    private final MessageService messageService;

    @Autowired
    @Setter
    private Clock clock;

    @Transactional(readOnly = true)
    public List<EmployeeCourseDto> getCoursesByEmployee(Long employeeId) {
        Employee employee = this.getEmployeeById(employeeId);

        return this.employeeCourseRepository.findByEmployee(employee).stream()
                .map(this.employeeCourseMapper::entityToDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeCourseDto> getPendingCoursesByEmployee(Long employeeId) {
        Employee employee = this.getEmployeeById(employeeId);

        return this.employeeCourseRepository.findByEmployeeAndStatus(employee, Status.ASSIGNED).stream()
                .map(this.employeeCourseMapper::entityToDto).toList();
    }

    public EmployeeCourseDto assignCourse(Long employeeId, Long courseId) {
        LOGGER.info("Assigning employee with ID {} to course with ID {}", employeeId, courseId);
        Employee employee = this.getEmployeeById(employeeId);
        Course course = this.getCourseById(courseId);

        boolean alreadyAssigned = this.employeeCourseRepository.existsByEmployeeAndCourse(employee, course);
        if (alreadyAssigned) {
            LOGGER.error("Employee with ID {} is already assigned to course with ID {}", employeeId, courseId);
            throw new IllegalStatusException(this.messageService.get("employee.course.is.already.assigned"));
        }

        EmployeeCourse employeeCourse = EmployeeCourse.builder()
                .employee(employee)
                .course(course)
                .assignedOn(LocalDate.now(this.clock))
                .status(Status.ASSIGNED)
                .build();

        EmployeeCourse savedEmployeeCourse = this.employeeCourseRepository.save(employeeCourse);
        LOGGER.debug("EmployeeCourse created with ID: {}", savedEmployeeCourse.getId());
        return this.employeeCourseMapper.entityToDto(savedEmployeeCourse);
    }

    public void markAsCompleted(Long employeeCourseId) {
        LOGGER.info("Marking as completed employeeCourse with ID: {}", employeeCourseId);
        EmployeeCourse employeeCourse = this.getEmployeeCourseById(employeeCourseId);

        if (employeeCourse.getStatus() != Status.ASSIGNED) {
            throw new IllegalStatusException(this.messageService.get("employee.course.mark.as.completed"));
        }

        LocalDate assignedDate = employeeCourse.getAssignedOn();
        long expirationDays = employeeCourse.getCourse().getExpirationDays();
        LocalDate expirationDate = assignedDate.plusDays(expirationDays);

        if (LocalDate.now(this.clock).isAfter(expirationDate)) {
            employeeCourse.setStatus(Status.EXPIRED);
        } else {
            employeeCourse.setStatus(Status.COMPLETED);
        }

        EmployeeCourse savedEmployeeCourse = this.employeeCourseRepository.save(employeeCourse);
        LOGGER.debug("EmployeeCourse with ID {} has been marked by status {}", savedEmployeeCourse.getId(),
                savedEmployeeCourse.getStatus());
    }

    private Employee getEmployeeById(Long employeeId) {
        return this.employeeRepository.findById(employeeId).orElseThrow(() -> {
            LOGGER.error("Employee with ID {} not exists", employeeId);
            return new ResourceNotFoundException(this.messageService.get("employee.not.found", employeeId));
        });
    }

    private Course getCourseById(Long courseId) {
        return this.courseRepository.findById(courseId).orElseThrow(() -> {
            LOGGER.error("Course with ID {} not exists", courseId);
            return new ResourceNotFoundException(this.messageService.get("course.not.found", courseId));
        });
    }

    private EmployeeCourse getEmployeeCourseById(Long employeeCourseId) {
        return this.employeeCourseRepository.findById(employeeCourseId).orElseThrow(() -> {
            LOGGER.error("EmployeeCourse with ID {} not exists", employeeCourseId);
            return new ResourceNotFoundException(this.messageService.get("employee.course.not.found"));
        });
    }

}
