package org.shrey.employee.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateOrUpdateEmployeeDTO {
    private String name;
    private Long managerId;
}
