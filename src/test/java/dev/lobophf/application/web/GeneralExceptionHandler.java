package dev.lobophf.application.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
          HttpHeaders headers, HttpStatus status, WebRequest request) {
          status = HttpStatus.UNPROCESSABLE_ENTITY;
      return super.handleMethodArgumentNotValid(ex, headers, status, request);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  private ResponseEntity<Object> handlerConflict(DataIntegrityViolationException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }
}
