package org.rmc.training_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class CourseWriteDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -1252732771545144046L;

    @NotBlank
    private String name;

    private String description;

    @Positive
    private Long expirationDays;

}
