package org.rmc.training_platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class EmployeeWriteDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 5301575633413646243L;

    @NotBlank(message = "{employee.name.not.empty}")
    private String name;

    @Email(message = "{employee.email.invalid}")
    @NotBlank(message = "{employee.email.not.empty}")
    private String email;

    @NotBlank(message = "{employee.department.not.empty}")
    private String department;

}
