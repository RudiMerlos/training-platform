package org.rmc.training_platform.dto;

import jakarta.validation.constraints.NotNull;
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
public class EmployeeCourseWriteDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 4023285279086584358L;

    @NotNull
    private Long employeeId;

    @NotNull
    private Long courseId;

}
