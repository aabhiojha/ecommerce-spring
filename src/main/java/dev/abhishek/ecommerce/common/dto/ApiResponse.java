package dev.abhishek.ecommerce.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API response envelope used for all endpoints.
 *
 * <p>Success responses populate {@code data} and {@code meta} (for paginated lists).
 * Error responses populate {@code error} with a machine-readable {@code code} and optional {@code details}.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private int statusCode;
    private String message;
    private T data;
    /** Pagination metadata — present only on paginated list endpoints. */
    private Object meta;
    /** Error detail — present only on error responses. */
    private Object error;
    private String path;
    private String method;
    private String timestamp;
    private String requestId;
}
