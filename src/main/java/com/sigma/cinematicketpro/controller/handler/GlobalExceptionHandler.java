package com.sigma.cinematicketpro.controller.handler;

import com.sigma.cinematicketpro.exception.ApiErrorResponse;
import com.sigma.cinematicketpro.exception.ResourceNotFoundException;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiErrorResponse handleDefaultException(Exception exception) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    return new ApiErrorResponse(
        status.value(),
        exception.getMessage()
    );
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiErrorResponse handleResourceNotFoundException(ResourceNotFoundException exception) {
    HttpStatus status = HttpStatus.NOT_FOUND;

    return new ApiErrorResponse(
        status.value(),
        exception.getMessage()
    );
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiErrorResponse handleValidationExceptions(MethodArgumentNotValidException exception) {
    Map<String, String> errors = getFieldsValidionErrorsMap(exception);

    return new ApiErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        "Validation issues occurred",
        errors
    );
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiErrorResponse handleBadCredentialsException(BadCredentialsException exception) {
    HttpStatus status = HttpStatus.UNAUTHORIZED;

    return new ApiErrorResponse(
        status.value(),
        exception.getMessage()
    );
  }

  @ExceptionHandler(RestClientException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiErrorResponse handleRestClientException(RestClientException exception) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    return new ApiErrorResponse(
        status.value(),
        exception.getMessage()
    );
  }

  private static Map<String, String> getFieldsValidionErrorsMap(MethodArgumentNotValidException exception) {
    return exception.getBindingResult()
        .getAllErrors()
        .stream()
        .filter(objectError -> objectError.getDefaultMessage() != null)
        .map(FieldError.class::cast)
        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
  }
}