package org.rmc.training_platform.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class CourseReadDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 6258456861188365464L;

    private Long id;

    private String name;

    private String description;

    private Long expirationDays;

}
