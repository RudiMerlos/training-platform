package org.rmc.training_platform.repository;

import org.rmc.training_platform.domain.Employee;
import org.rmc.training_platform.domain.EmployeeCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeCourseRepository extends JpaRepository<Long, EmployeeCourse> {

    List<EmployeeCourse> findByEmployee(Employee employee);

}
