package com.claire.firstspring.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler({
        IllegalArgumentException.class,
        IllegalStateException.class
    })
    protected ResponseEntity<Object> badRequest(RuntimeException ex) {
        String bodyOfResponse = ExceptionUtils.getStackTrace(ex);
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NullPointerException.class})
    protected ResponseEntity<Object> nullPointerException(NullPointerException ex) {
        String message = ex.getMessage();
        if (StringUtils.startsWith(message, "client-error:")) {
            return badRequest(ex);
        } else {
            String bodyOfResponse = ExceptionUtils.getStackTrace(ex);
            return new ResponseEntity<>(bodyOfResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler({NoSuchElementException.class})
    protected ResponseEntity<String> notFound(NoSuchElementException ex) {
        String bodyOfResponse = ExceptionUtils.getStackTrace(ex);
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({RuntimeException.class})
    protected ResponseEntity<String> runTimeException(RuntimeException ex) {
        String bodyOfResponse = ExceptionUtils.getStackTrace(ex);
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}