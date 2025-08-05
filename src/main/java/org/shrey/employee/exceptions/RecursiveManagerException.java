package org.shrey.employee.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RecursiveManagerException extends RuntimeException {

    public RecursiveManagerException() {
        super("Manager cannot have recursive cycles");
    }
}
