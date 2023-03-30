package com.sas.sso.exception;

import com.sas.sso.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_MSG = "Something went Wrong, Please try Later";

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(Exception ex) {
        log.error("Exception  occurs => {}", ex.toString());
        return new ResponseEntity<>(new Response(ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseBody
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        log.error("BadRequestException occurs => {}", ex.toString());
        return new ResponseEntity<>(new Response(ex.getMessage(), ex.getResponseObject(), ex.getHttpStatus()),
                HttpStatus.OK);
    }

    @ExceptionHandler(value = ObjectNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Object> handleObjectNotFoundException(ObjectNotFoundException ex) {
        log.error("ObjectNotFoundException occurs => {}", ex.toString());
        return new ResponseEntity<>(new Response(ex.getMessage(), ex.getResponseObject(), ex.getHttpStatus()),
                HttpStatus.OK);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(HttpRequestMethodNotSupportedException ex) {
        log.error("HttpRequestMethodNotSupportedException occurs => {}", ex.toString());
        return new ResponseEntity<>(new Response(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException occurred ", ex);
        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        return new ResponseEntity<>(
                new Response("Please pass all mandatory attributes", errors, HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException occurred ", ex);
        return new ResponseEntity<>(new Response("Invalid request body", HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(MethodArgumentTypeMismatchException ex) {
        log.error("MethodArgumentTypeMismatchException occurred ", ex);
        return new ResponseEntity<>(new Response("Invalid params/attributes passed ", HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(AccessDeniedException ex) {
        log.error("AccessDeniedException occurred ", ex);
        return new ResponseEntity<>(new Response("Access Denied", HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
    }


}