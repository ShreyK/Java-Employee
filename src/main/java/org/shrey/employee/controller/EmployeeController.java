package org.shrey.employee.controller;

import lombok.extern.slf4j.Slf4j;
import org.shrey.employee.exceptions.EmployeeNotFound;
import org.shrey.employee.exceptions.ManagerException;
import org.shrey.employee.exceptions.RecursiveManagerException;
import org.shrey.employee.model.CreateOrUpdateEmployeeDTO;
import org.shrey.employee.model.EmployeeDTO;
import org.shrey.employee.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @ExceptionHandler(EmployeeNotFound.class)
    public ErrorResponse handleEmployeeNotFound(EmployeeNotFound e) {
        log.error("Employee not found", e);
        return ErrorResponse.create(e, HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ManagerException.class)
    public ErrorResponse handleManagerException(ManagerException e) {
        log.error("Manager cannot be same as employee", e);
        return ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(RecursiveManagerException.class)
    public ErrorResponse handleRecursiveManagerException(RecursiveManagerException e) {
        log.error("Manager cannot have recursive cycle", e);
        return ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long employeeId) {
        log.info("getEmployee({})", employeeId);
        return ResponseEntity.ok(employeeService.getEmployee(employeeId));
    }

    @GetMapping("/{employeeId}/subordinate")
    public ResponseEntity<List<EmployeeDTO>> getSubordinates(@PathVariable Long employeeId) {
        log.info("getSubordinates() of employee: {}", employeeId);
        return ResponseEntity.ok(employeeService.getSubordinates(employeeId));
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeDTO>> getAllEmployees(Pageable pageable) {
        log.info("getAllEmployees({})", pageable);
        return ResponseEntity.ok(employeeService.getEmployees(pageable));
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody CreateOrUpdateEmployeeDTO employeeDTO) {
        log.info("createEmployee({})", employeeDTO);
        return ResponseEntity.ok(employeeService.createEmployee(employeeDTO));
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long employeeId, @RequestBody CreateOrUpdateEmployeeDTO employeeDTO) {
        log.info("updateEmployee({}, {})", employeeId, employeeDTO);
        return ResponseEntity.ok(employeeService.updateEmployee(employeeId, employeeDTO));
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long employeeId) {
        log.info("deleteEmployee({})", employeeId);
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }
}
