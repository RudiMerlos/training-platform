package org.rmc.training_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeReadDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -7155685466888738752L;

    private Long id;

    private String name;

    private String email;

    private String department;

}
