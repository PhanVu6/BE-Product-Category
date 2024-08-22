package com.example.managerproduct.exception;


import com.example.managerproduct.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    // Xử lý AppException
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<String>> handleAppException(AppException exception, HttpServletRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(messageSource.getMessage(errorCode.getMessage(), null, locale));
        apiResponse.setResult(exception.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(apiResponse);
    }

    // Xử lý MethodArgumentNotValidException bắt trường lỗi
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException exception) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = messageSource.getMessage(error.getDefaultMessage(), null, locale);
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>();
        apiResponse.setCode(HttpStatus.BAD_REQUEST.value());
        apiResponse.setMessage(messageSource.getMessage("error.invalidInput", null, locale));
        apiResponse.setResult(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    // Xử lý HttpMessageNotReadableException bắt lỗi format json
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage("error.invalidJson", null, locale);

        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setCode(HttpStatus.BAD_REQUEST.value());
        apiResponse.setMessage(errorMessage);
        apiResponse.setResult(exception.getMessage());

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    // Xử lý RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage("error.uncategorized", null, locale);

        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        apiResponse.setMessage(errorMessage);
        apiResponse.setResult(ex.getMessage());

        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Bắt lỗi ràng bộng đối số truyền vào trên phương thức
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(
//            ConstraintViolationException exception) {
//
//        Locale locale = LocaleContextHolder.getLocale();
//        Map<String, String> errors = new HashMap<>();
//
//        exception.getConstraintViolations().forEach(violation -> {
//            String propertyPath = violation.getPropertyPath().toString();
//            String errorMessage = messageSource.getMessage(violation.getMessage(), null, locale);
//            errors.put(propertyPath, errorMessage);
//        });
//
//        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>();
//        apiResponse.setCode(HttpStatus.BAD_REQUEST.value());
//        apiResponse.setMessage(messageSource.getMessage("error.invalidInput", null, locale));
//        apiResponse.setResult(errors);
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
//    }
}
