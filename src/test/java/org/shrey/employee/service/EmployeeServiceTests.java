package org.shrey.employee.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.shrey.employee.exceptions.EmployeeNotFound;
import org.shrey.employee.exceptions.RecursiveManagerException;
import org.shrey.employee.model.CreateOrUpdateEmployeeDTO;
import org.shrey.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(EmployeeService.class)
public class EmployeeServiceTests {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void createEmployee() {
        CreateOrUpdateEmployeeDTO newEmployee = new CreateOrUpdateEmployeeDTO("Employee #1", null);
        employeeService.createEmployee(newEmployee);

        Assertions.assertThat(employeeRepository.existsById(1L)).isTrue();
        Assertions.assertThat(employeeRepository.findById(1L).get().getName()).isEqualTo("Employee #1");
        Assertions.assertThat(employeeRepository.findById(1L).get().getManager()).isNull();
    }

    @Test
    public void updateEmployee() {
        CreateOrUpdateEmployeeDTO firstEmployee = new CreateOrUpdateEmployeeDTO("Employee #1", null);
        employeeService.createEmployee(firstEmployee);
        CreateOrUpdateEmployeeDTO secondEmployee = new CreateOrUpdateEmployeeDTO("Employee #2", 1L);
        employeeService.createEmployee(secondEmployee);

        Assertions.assertThat(employeeRepository.existsById(1L)).isTrue();
        Assertions.assertThat(employeeRepository.existsById(2L)).isTrue();
        Assertions.assertThat(employeeRepository.findById(2L).get().getName()).isEqualTo("Employee #2");
        Assertions.assertThat(employeeRepository.findById(1L).get().getManager()).isNull();
        Assertions.assertThat(employeeRepository.findById(2L).get().getManager().getName()).isEqualTo("Employee #1");
    }

    @Test
    public void deleteEmployee() {
        // Tests cascade nature of employee deletion
        CreateOrUpdateEmployeeDTO firstEmployee = new CreateOrUpdateEmployeeDTO("Employee #1", null);
        employeeService.createEmployee(firstEmployee);
        CreateOrUpdateEmployeeDTO secondEmployee = new CreateOrUpdateEmployeeDTO("Employee #2", 1L);
        employeeService.createEmployee(secondEmployee);

        employeeService.deleteEmployee(1L);
        Assertions.assertThatThrownBy(() -> employeeService.getEmployee(1L)).isInstanceOf(EmployeeNotFound.class);
        Assertions.assertThat(employeeService.getEmployee(2L).getName()).isEqualTo("Employee #2");
        Assertions.assertThat(employeeService.getEmployee(2L).getManager()).isEqualTo(null);
    }

    @Test
    public void updateEmployeeMangerCycle() {
        CreateOrUpdateEmployeeDTO firstEmployee = new CreateOrUpdateEmployeeDTO("Employee #1", null);
        employeeService.createEmployee(firstEmployee);
        CreateOrUpdateEmployeeDTO secondEmployee = new CreateOrUpdateEmployeeDTO("Employee #2", 1L);
        employeeService.createEmployee(secondEmployee);

        Assertions.assertThat(employeeRepository.existsById(1L)).isTrue();
        Assertions.assertThat(employeeRepository.existsById(2L)).isTrue();
        Assertions.assertThat(employeeRepository.findById(2L).get().getName()).isEqualTo("Employee #2");
        Assertions.assertThat(employeeRepository.findById(1L).get().getManager()).isNull();
        Assertions.assertThat(employeeRepository.findById(2L).get().getManager().getName()).isEqualTo("Employee #1");

        CreateOrUpdateEmployeeDTO firstEmployeeUpdateManagerCycle = new CreateOrUpdateEmployeeDTO("Employee #1", 2L);
        Assertions.assertThatThrownBy(() -> employeeService.updateEmployee(1L, firstEmployeeUpdateManagerCycle)).isInstanceOf(RecursiveManagerException.class);
        Assertions.assertThat(employeeRepository.findById(1L).get().getManager()).isNull();
    }
}
