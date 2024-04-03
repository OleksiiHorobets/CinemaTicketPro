package com.sigma.cinematicketpro.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

  private int code;
  private String message;
  private Object data;

  public ApiErrorResponse(int code, String message) {
    this.code = code;
    this.message = message;
  }
}