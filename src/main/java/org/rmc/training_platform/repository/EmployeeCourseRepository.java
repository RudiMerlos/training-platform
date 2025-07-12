package org.rmc.training_platform.repository;

import org.rmc.training_platform.domain.Course;
import org.rmc.training_platform.domain.Employee;
import org.rmc.training_platform.domain.EmployeeCourse;
import org.rmc.training_platform.domain.enumeration.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeCourseRepository extends JpaRepository<EmployeeCourse, Long> {

    List<EmployeeCourse> findByEmployee(Employee employee);

    List<EmployeeCourse> findByEmployeeAndStatus(Employee employee, Status status);

    boolean existsByEmployeeAndCourse(Employee employee, Course course);

}
