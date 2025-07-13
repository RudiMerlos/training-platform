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
public class CourseReadDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 6258456861188365464L;

    private Long id;

    private String name;

    private String description;

    private Long expirationDays;

}
