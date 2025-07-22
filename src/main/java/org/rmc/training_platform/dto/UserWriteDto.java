package org.rmc.training_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rmc.training_platform.domain.enumeration.Role;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWriteDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -4689472812044432775L;

    @NotBlank(message = "{user.username.required}")
    private String username;

    @NotBlank(message = "{user.password.required}")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message="{user.password.valid}")
    private String password;

    @NotEmpty(message = "{user.roles.required}")
    private Set<Role> roles;

}
