package com.claire.firstspring.web;

import com.claire.firstspring.web.model.WebError;
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
    protected ResponseEntity<WebError> badRequest(RuntimeException ex) {
        String bodyOfResponse = ExceptionUtils.getStackTrace(ex);
        return new ResponseEntity<>(WebError.of(bodyOfResponse), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NullPointerException.class})
    protected ResponseEntity<WebError> nullPointerException(NullPointerException ex) {
        String message = ex.getMessage();
        if (StringUtils.startsWith(message, "client-error:")) {
            return badRequest(ex);
        } else {
            String bodyOfResponse = ExceptionUtils.getStackTrace(ex);
            return new ResponseEntity<>(
                WebError.of(bodyOfResponse),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @ExceptionHandler({NoSuchElementException.class})
    protected ResponseEntity<WebError> notFound(NoSuchElementException ex) {
        String bodyOfResponse = ExceptionUtils.getStackTrace(ex);
        return new ResponseEntity<>(
            WebError.of(bodyOfResponse),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({RuntimeException.class})
    protected ResponseEntity<WebError> runTimeException(RuntimeException ex) {
        String bodyOfResponse = ExceptionUtils.getStackTrace(ex);
        return new ResponseEntity<>(
            WebError.of(bodyOfResponse),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}