package org.rmc.training_platform.repository;

import org.rmc.training_platform.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Long, Course> {
}
