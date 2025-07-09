package org.rmc.training_platform.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.rmc.training_platform.domain.enumeration.Status;

import java.io.Serial;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Entity
@Table(name = "employee_course", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "course_id"}))
public class EmployeeCourse extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 4709872887164457893L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(nullable = false)
    private LocalDate assignedOn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

}
