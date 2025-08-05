package org.shrey.employee.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.shrey.employee.exceptions.EmployeeNotFound;
import org.shrey.employee.exceptions.ManagerException;
import org.shrey.employee.exceptions.RecursiveManagerException;
import org.shrey.employee.model.CreateOrUpdateEmployeeDTO;
import org.shrey.employee.model.Employee;
import org.shrey.employee.model.EmployeeDTO;
import org.shrey.employee.model.ManagerDTO;
import org.shrey.employee.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        ManagerDTO managerDTO = null;
        if (employee.getManager() != null) {
            managerDTO = new ManagerDTO(employee.getManager().getId(), employee.getManager().getName());
        }
        return new EmployeeDTO(employee.getId(), employee.getName(), managerDTO);
    }

    public Page<EmployeeDTO> getEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::convertToDTO);
    }

    public EmployeeDTO getEmployee(Long employeeId) throws EmployeeNotFound {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if(employee.isEmpty()) {
            throw new EmployeeNotFound();
        }
        log.info("getEmployee({})", employee);
        return convertToDTO(employee.get());
    }

    public List<EmployeeDTO> getSubordinates(Long employeeId) throws EmployeeNotFound {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFound();
        }
        return employeeRepository.findByManagerId(employeeId).stream()
                .map(this::convertToDTO).collect(Collectors.toList());
    }

    private boolean checkRecursiveManager(Employee employee, Employee manager) {
        Employee current = manager;
        while(current != null) {
            if (current.getId().equals(employee.getId())) {
                return true;
            }
            current =  current.getManager();
        }
        return false;
    }

    @Transactional
    public EmployeeDTO createEmployee(CreateOrUpdateEmployeeDTO employeeDTO) throws ManagerException{
        Employee employee = new Employee();
        employee.setName(employeeDTO.getName());
        if (employeeDTO.getManagerId() != null) {
            if (employeeDTO.getManagerId().equals(employee.getId())) {
                throw new ManagerException();
            }
            Employee manager = employeeRepository.findById(employeeDTO.getManagerId()).orElse(null);
            if (checkRecursiveManager(employee, manager)) {
                log.info("Could not add managerId({}) as there is a recursive cycle", employeeDTO.getManagerId());
                throw new RecursiveManagerException();
            }
            if (manager == null) {
                log.info("Could not add managerId({}) does not exist", employeeDTO.getManagerId());
            }
            employee.setManager(manager);
        }
        log.info("createEmployee({})", employee);
        return convertToDTO(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeDTO updateEmployee(Long id, CreateOrUpdateEmployeeDTO employeeDTO) throws EmployeeNotFound, ManagerException {
        return employeeRepository.findById(id).map(
                employee -> {
                    employee.setName(employeeDTO.getName());
                    if (employeeDTO.getManagerId() != null) {
                        if (employeeDTO.getManagerId().equals(employee.getId())) {
                            throw new ManagerException();
                        }
                        Employee manager = employeeRepository.findById(employeeDTO.getManagerId()).orElse(null);
                        if (checkRecursiveManager(employee, manager)) {
                            log.info("Could not add managerId({}) as there is a recursive cycle", employeeDTO.getManagerId());
                            throw new RecursiveManagerException();
                        }
                        employee.setManager(employeeRepository.findById(employeeDTO.getManagerId()).orElse(null));
                    } else {
                        employee.setManager(null);
                    }
                    log.info("updateEmployee({})", employee);
                    return convertToDTO(employeeRepository.save(employee));
                }
        ).orElseThrow(EmployeeNotFound::new);
    }

    @Transactional
    public void deleteEmployee(Long employeeId) throws EmployeeNotFound {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFound();
        }
        List<Employee> subordinates = employeeRepository.findByManagerId(employeeId);
        for (Employee subordinate : subordinates) {
            subordinate.setManager(null);
        }
        log.info("deleteEmployee({})", employeeId);
        employeeRepository.deleteById(employeeId);
    }

}
