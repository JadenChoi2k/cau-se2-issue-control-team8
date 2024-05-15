package org.cause2.team8.controller;

import org.cause2.team8.common.utils.exceptions.ErrorBase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorControllerAdvice {

    @ExceptionHandler(ErrorBase.class)
    public ResponseEntity<?> handleException(ErrorBase e) {
        return ResponseEntity.status(e.getStatusCode()).body(e.getBody());
    }
}
