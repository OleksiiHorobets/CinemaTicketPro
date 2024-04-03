package com.sigma.cinematicketpro.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;
import java.util.regex.Pattern;

public class UriPosterValidator implements ConstraintValidator<ValidPosterUri, URI> {

  @Override
  public boolean isValid(URI uri, ConstraintValidatorContext context) {
    if (uri == null) {
      return false;
    }
    final String URI_VALIDATION_REGEX = "^(?=.{1,300}$).*\\.(?i:jpg|jpeg|png|webp)$";
    return Pattern.matches(URI_VALIDATION_REGEX, uri.toString());
  }
}