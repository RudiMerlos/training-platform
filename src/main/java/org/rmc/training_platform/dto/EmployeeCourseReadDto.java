package org.rmc.training_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rmc.training_platform.domain.enumeration.Status;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
