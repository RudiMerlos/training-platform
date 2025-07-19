package org.rmc.training_platform.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.rmc.training_platform.security.annotations.RoleUser;
import org.rmc.training_platform.dto.EmployeeReadDto;
import org.rmc.training_platform.dto.EmployeeWriteDto;
import org.rmc.training_platform.service.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employees", description = "Employee related operations.")
public class EmployeeController extends CrudBaseController<EmployeeWriteDto, EmployeeReadDto> {

    private final EmployeeService employeeService;

    public EmployeeController(final EmployeeService employeeService) {
        super(employeeService);
        this.employeeService = employeeService;
    }

    @RoleUser
    @GetMapping("/by-email")
    public EmployeeReadDto getByEmail(@RequestParam String email) {
        return this.employeeService.getByEmail(email);
    }

}
