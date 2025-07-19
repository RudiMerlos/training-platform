package org.rmc.training_platform.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -611047015057351691L;

    private String username;

    private String password;

}
