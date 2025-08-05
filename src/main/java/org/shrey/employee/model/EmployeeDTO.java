package org.shrey.employee.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeDTO {
    private Long id;
    private String name;
    private ManagerDTO manager;
}
