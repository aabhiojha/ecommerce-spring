package dev.abhishek.ecommerce.common.exceptions;

import dev.abhishek.ecommerce.common.dto.ApiResponse;
import dev.abhishek.ecommerce.common.storage.StorageException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Resource not found")
    @ExceptionHandler({ProductNotFoundException.class, CategoryNotFoundException.class, ResourceNotFoundException.class,
            UsernameNotFoundException.class})
    public ResponseEntity<dev.abhishek.ecommerce.common.dto.ApiResponse<Object>> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), "NOT_FOUND", null, request);
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request")
    @ExceptionHandler({IllegalArgumentException.class, InsufficientProductInventoryException.class})
    public ResponseEntity<dev.abhishek.ecommerce.common.dto.ApiResponse<Object>> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), "BAD_REQUEST", null, request);
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<dev.abhishek.ecommerce.common.dto.ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<Map<String, String>> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("field", error.getField());
                    map.put("constraint", error.getDefaultMessage());
                    return map;
                })
                .collect(Collectors.toList());

        return build(HttpStatus.BAD_REQUEST, "Validation failed", "VALIDATION_ERROR", details, request);
    }

    /**
     * Handles unauthenticated access (e.g. missing or invalid JWT token).
     */
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<dev.abhishek.ecommerce.common.dto.ApiResponse<Object>> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Authentication required.", "UNAUTHORIZED", null, request);
    }

    /**
     * Thrown by services that check ownership themselves; without this it would surface as a 500.
     */
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<dev.abhishek.ecommerce.common.dto.ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "You do not have permission to perform this action.", "ACCESS_DENIED", null, request);
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict")
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<dev.abhishek.ecommerce.common.dto.ApiResponse<Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        log.warn("Data integrity violation: path={}", request.getRequestURI(), ex);
        return build(HttpStatus.CONFLICT, "The request conflicts with existing data.", "DATA_CONFLICT", null, request);
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "413", description = "Payload too large")
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<dev.abhishek.ecommerce.common.dto.ApiResponse<Object>> handleUploadTooLarge(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "The uploaded file is too large.", "PAYLOAD_TOO_LARGE", null, request);
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Service unavailable")
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<dev.abhishek.ecommerce.common.dto.ApiResponse<Object>> handleStorageException(StorageException ex, HttpServletRequest request) {
        log.error("Object storage failure: path={}", request.getRequestURI(), ex);
        return build(HttpStatus.SERVICE_UNAVAILABLE, "File storage is currently unavailable.", "STORAGE_ERROR", null, request);
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    @ExceptionHandler(Exception.class)
    public ResponseEntity<dev.abhishek.ecommerce.common.dto.ApiResponse<Object>> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception: method={}, path={}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again later.", "INTERNAL_ERROR", null, request);
    }

    private ResponseEntity<ApiResponse<Object>> build(HttpStatus status, String message, String errorCode, Object details, HttpServletRequest request) {
        Map<String, Object> errorObject = new LinkedHashMap<>();
        errorObject.put("code", errorCode);
        errorObject.put("details", details);

        String requestId = (String) request.getAttribute("requestId");

        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .statusCode(status.value())
                .message(message)
                .error(errorObject)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(Instant.now().toString())
                .requestId(requestId)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
