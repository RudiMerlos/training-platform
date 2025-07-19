package org.rmc.training_platform.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoleType {

    ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

    private final String name;

}
