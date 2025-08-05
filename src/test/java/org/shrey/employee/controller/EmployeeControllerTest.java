package org.shrey.employee.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.shrey.employee.model.CreateOrUpdateEmployeeDTO;
import org.shrey.employee.model.EmployeeDTO;
import org.shrey.employee.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class EmployeeControllerTest {

    @InjectMocks
    EmployeeController mockEmployeeController;

    @Mock
    EmployeeService employeeService;

    @Test
    void testCreate() {
        CreateOrUpdateEmployeeDTO newEmployee = new CreateOrUpdateEmployeeDTO("Employee #1", null);
        ResponseEntity<Void> actual =  mockEmployeeController.createEmployee(newEmployee);
        Assertions.assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}