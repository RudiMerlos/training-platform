package org.rmc.training_platform.repository;

import org.rmc.training_platform.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Long, Employee> {
}
