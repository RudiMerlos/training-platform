package org.rmc.training_platform.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class EmployeeReadDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -7155685466888738752L;

    private Long id;

    private String name;

    private String email;

    private String department;

}
