package org.rmc.training_platform.domain.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Employee {

    private Long id;

    private String name;

    private String email;

    private String department;

}
