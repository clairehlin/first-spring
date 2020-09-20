package com.claire.firstspring.web;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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

    @ExceptionHandler({NoSuchElementException.class})
    protected ResponseEntity<String> notFound(NoSuchElementException ex) {
        String bodyOfResponse = ExceptionUtils.getStackTrace(ex);
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.NOT_FOUND);
    }
}