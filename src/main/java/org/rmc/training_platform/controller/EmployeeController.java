package org.rmc.training_platform.controller;

import org.rmc.training_platform.dto.EmployeeReadDto;
import org.rmc.training_platform.dto.EmployeeWriteDto;
import org.rmc.training_platform.service.EmployeeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController extends CrudBaseController<EmployeeWriteDto, EmployeeReadDto> {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        super(employeeService);
        this.employeeService = employeeService;
    }

    @GetMapping("/by-email")
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    public EmployeeReadDto getByEmail(@RequestParam String email) {
        return this.employeeService.getByEmail(email);
    }

}
