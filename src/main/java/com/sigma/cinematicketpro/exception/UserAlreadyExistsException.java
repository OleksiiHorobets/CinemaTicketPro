package com.sigma.cinematicketpro.exception;

import java.util.List;
import lombok.Getter;

public class UserAlreadyExistsException extends RuntimeException {

  @Getter
  private final List<String> errorsList;

  public UserAlreadyExistsException(String message, List<String> errors) {
    super(message);
    errorsList = errors;
  }
}