package org.rmc.training_platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Entity
public class Course extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -2478414991877691277L;

    @NotBlank
    private String name;

    private String description;

    @Positive
    private Long expirationDays;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<EmployeeCourse> employeeCourses;

}
