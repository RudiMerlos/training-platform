package org.rmc.training_platform.security.repository;

import org.rmc.training_platform.security.domain.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserApp, Long> {

    Optional<UserApp> findByUsername(String username);

}
