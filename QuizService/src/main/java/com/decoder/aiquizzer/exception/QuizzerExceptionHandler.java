package com.decoder.aiquizzer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class QuizzerExceptionHandler {

    @ExceptionHandler(QuizzerException.class)
    public ResponseEntity<?> handleQuizzerEception(QuizzerException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> hadnleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("invalid value for difficulty. accepted values: EASY/MEDIUM/HARD"));
    }

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<?> handleInvalidJwtException(InvalidJwtException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CredentialException.class)
    public ResponseEntity<?> handlerCredentialException(CredentialException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(OTPException.class)
    public ResponseEntity<?> handleOTPException(OTPException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    public static class ErrorResponse {
        String ErrorMessage;

        public ErrorResponse(String message) {
            this.ErrorMessage = message;
        }

        public String getErrorMessage() {
            return this.ErrorMessage;
        }
    }
}
