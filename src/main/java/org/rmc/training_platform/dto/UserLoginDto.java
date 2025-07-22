package org.rmc.training_platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLoginDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -611047015057351691L;

    @NotBlank(message = "{user.username.required}")
    private String username;

    @NotBlank(message = "{user.password.required}")
    private String password;

}
