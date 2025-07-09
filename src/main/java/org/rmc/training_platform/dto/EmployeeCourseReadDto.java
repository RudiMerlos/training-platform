package org.rmc.training_platform.dto;

import lombok.Builder;
import lombok.Data;
import org.rmc.training_platform.domain.enumeration.Status;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
public class EmployeeCourseReadDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -1197535376671302036L;

    private Long id;

    private Long employeeId;

    private Long courseId;

    private LocalDate assignedOn;

    private Status status;

}
