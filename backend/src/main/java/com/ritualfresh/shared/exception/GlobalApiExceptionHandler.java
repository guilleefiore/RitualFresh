package com.ritualfresh.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.ritualfresh.shared.exception.BusinessRuleException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalApiExceptionHandler {
    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse manejarReglaBusiness(BusinessRuleException excepcion) {
        return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), excepcion.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse manejarValidation(MethodArgumentNotValidException excepcion) {
        String mensaje = excepcion.getBindingResult().getFieldErrors().stream()
                .map(error -> "Debe completar correctamente el campo " + error.getField() + ".")
                .distinct()
                .collect(Collectors.joining(" "));

        return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), mensaje);
    }
}
