package org.rmc.training_platform.repository;

import org.rmc.training_platform.domain.Role;
import org.rmc.training_platform.domain.enumeration.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRole(RoleType role);

}
