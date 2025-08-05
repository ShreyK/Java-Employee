# Java + Hibernate + Postgres App
Builds an employee table with manager + subordinate mapping

## Setup
Ensure you have PostgreSQL installed locally

## Capabilities
### Get `/employee`
- Fetches all the employees in a list

### Get `/employee/{employeeId}`
- Fetches the specified employee

### Get `/employee/{employeeId}/subordinate`
- Fetches the specified employee's subordinates

### Post `/employee`
- managerId can be nullable
```
    name: "Employee #1"
    managerId: 1
```

### Put `/employee/{employeeId}`
```
    name: "Employee #2"
    managerId: null
```

### Delete `/employee/{employeeId}`
- Deletes the employee
- Ensures any subordinates have their managerID updated to NULL

