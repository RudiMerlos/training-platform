package org.rmc.training_platform.security.repository;

import org.rmc.training_platform.security.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
