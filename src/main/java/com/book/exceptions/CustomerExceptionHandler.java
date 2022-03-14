package com.book.exceptions;

import java.security.SignatureException;
import java.util.Date;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class CustomerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage validationException(Exception exception, WebRequest webRequest) {
        return ErrorMessage.builder()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .timestamp(new Date())
            .message(exception.getMessage())
            .description(webRequest.getDescription(false))
            .build();
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage duplicateEmailException(Exception exception, WebRequest webRequest) {
        return ErrorMessage.builder()
            .statusCode(HttpStatus.CONFLICT.value())
            .timestamp(new Date())
            .message("The email address is already in the records. Please try different an email address")
            .description(webRequest.getDescription(false))
            .build();
    }

    @ExceptionHandler(value = {SignatureException.class, BadCredentialsException.class})
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorMessage accessDeniedException(Exception exception, WebRequest webRequest) {
        return ErrorMessage.builder()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .timestamp(new Date())
            .message("Access is denied!")
            .description(webRequest.getDescription(false))
            .build();
    }
}
