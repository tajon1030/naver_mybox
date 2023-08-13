package com.numble.mybox.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ErrorResponse> handleException(CustomException e) {
        log.error("Error occurs: {}", e.toString());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(new ErrorResponse(e.getErrorCode()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> applicationHandler(RuntimeException e) {
        log.error("Error occurs: {}", e.toString());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> validException(ConstraintViolationException e) {
        log.error("Error occurs: {}", e.toString());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code("COMMON-ERR-400")
                .message("유효성 검사 실패 : " + e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
