package dev.abhishek.ecommerce.common.exceptions;

import dev.abhishek.ecommerce.common.dto.ApiResponse;
import dev.abhishek.ecommerce.common.dto.PagedResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "dev.abhishek.ecommerce.modules")
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Skip if return type is already an ApiResponse — don't double-wrap
        Class<?> paramType = returnType.getParameterType();
        if (ApiResponse.class.isAssignableFrom(paramType)) {
            return false;
        }
        // Skip String return types — StringHttpMessageConverter is selected for these and
        // cannot serialize a POJO; trying would cause a ClassCastException at runtime.
        if (String.class.isAssignableFrom(paramType)) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // Don't wrap if the body is already an ApiResponse (from GlobalExceptionHandler path)
        if (body instanceof ApiResponse) {
            return body;
        }

        // Don't wrap null bodies (e.g., ResponseEntity<Void> from 204 DELETE endpoints)
        // The HTTP 204 will be sent correctly; no JSON body needed.
        if (body == null) {
            return null;
        }

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();

        String path = servletRequest.getRequestURI();
        String method = servletRequest.getMethod();
        String requestId = (String) servletRequest.getAttribute("requestId");

        // Read the real status code the controller set (e.g. 201, 200, 202)
        int statusCode = servletResponse.getStatus();
        HttpStatus httpStatus = HttpStatus.resolve(statusCode);
        if (httpStatus == null) httpStatus = HttpStatus.OK;

        Object data = body;
        Object meta = null;

        // Unpack PagedResponse into data + meta
        if (body instanceof PagedResponse<?> pagedResponse) {
            data = pagedResponse.getContent();
            Map<String, Object> metaMap = new LinkedHashMap<>();
            // Convert 0-indexed pageNo to 1-indexed for the client
            metaMap.put("page", pagedResponse.getPageNo() + 1);
            metaMap.put("limit", pagedResponse.getPageSize());
            metaMap.put("total", pagedResponse.getTotalElements());
            metaMap.put("totalPages", pagedResponse.getTotalPages());
            meta = metaMap;
        }

        // Build a contextual message
        String message;
        if (httpStatus == HttpStatus.CREATED) {
            message = "Resource created successfully";
        } else if ("DELETE".equalsIgnoreCase(method)) {
            message = "Resource deleted successfully";
        } else if ("GET".equalsIgnoreCase(method)) {
            message = "Data retrieved successfully";
        } else {
            message = "Request processed successfully";
        }

        return ApiResponse.builder()
                .success(true)
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .meta(meta)
                .path(path)
                .method(method)
                .timestamp(Instant.now().toString())
                .requestId(requestId)
                .build();
    }
}
