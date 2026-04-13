package com.example.backend.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // Validation for @Valid annotation
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValueExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new LinkedHashMap<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("status", 400);
    response.put("message", "Validation failed");
    response.put("errors", errors);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  // Validation for unknown fields in JSON request body
  @ExceptionHandler(UnrecognizedPropertyException.class)
  public ResponseEntity<?> handleUnknownField(UnrecognizedPropertyException ex) {
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("status", 400);
    response.put("message", "Validation failed");
    response.put("error", "Unknown field: " + ex.getPropertyName());

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  // Validation for malformed JSON / invalid enum values in request body
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleNotReadable(HttpMessageNotReadableException ex) {
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("status", 400);
    response.put("message", "Validation failed");

    Throwable cause = ex.getMostSpecificCause();
    if (cause instanceof InvalidFormatException ife) {
      String field = ife.getPath().stream()
          .map(ref -> ref.getFieldName())
          .collect(Collectors.joining("."));

      String error = "Invalid value";
      if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
        String acceptedValues = java.util.Arrays.stream(ife.getTargetType().getEnumConstants())
            .map(String::valueOf)
            .collect(Collectors.joining(", "));
        error = String.format(
            "Invalid value '%s' for field '%s'. Accepted values: [%s]",
            ife.getValue(),
            field,
            acceptedValues);
      } else if (!field.isBlank()) {
        error = String.format("Invalid value '%s' for field '%s'", ife.getValue(), field);
      }
      response.put("error", error);
    } else {
      response.put("error", cause != null ? cause.getMessage() : ex.getMessage());
    }

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConcurrentBidException.class)
  public ResponseEntity<?> handleConcurrentBid(ConcurrentBidException ex) {
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("status", 409);
    response.put("message", ex.getMessage());
    response.put("productId", ex.getProductId());
    response.put("currentPrice", ex.getCurrentPrice());
    response.put("priceStep", ex.getPriceStep());
    response.put("highestBidderId", ex.getHighestBidderId());

    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }
}
