package org.shrey.employee.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ManagerException extends RuntimeException {

    public ManagerException() {
        super("Manager cannot be same as employee");
    }
}
