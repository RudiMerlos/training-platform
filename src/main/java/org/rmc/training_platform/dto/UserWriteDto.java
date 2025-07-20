package org.rmc.training_platform.dto;

import lombok.Data;
import org.rmc.training_platform.domain.enumeration.Role;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Data
public class UserWriteDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -4689472812044432775L;

    private String username;

    private String password;

    private Set<Role> roles;

}
