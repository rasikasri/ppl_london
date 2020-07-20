package com.london.ws.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    private ResponseEntity handleValidationException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return  new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
