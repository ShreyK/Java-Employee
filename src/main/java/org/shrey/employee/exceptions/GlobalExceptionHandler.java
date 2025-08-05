package org.shrey.employee.exceptions;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleBaseRuntimeException(RuntimeException e) {
        log.error("Runtime Exception", e);
        return ErrorResponse.create(e, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
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

}
