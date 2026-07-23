package com.procurement.enterprise.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper for all REST APIs.
 *
 * @param <T> response data type
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;

    private final String message;

    private final T data;

    private final int status;

    private final LocalDateTime timestamp;


    private ApiResponse(
            boolean success,
            String message,
            T data,
            HttpStatus httpStatus
    ) {

        this.success = success;
        this.message = message;
        this.data = data;
        this.status = httpStatus.value();
        this.timestamp = LocalDateTime.now();
    }


    /**
     * Success response with HTTP 200.
     */
    public static <T> ApiResponse<T> success(
            String message,
            T data
    ) {

        return new ApiResponse<>(
                true,
                message,
                data,
                HttpStatus.OK
        );
    }


    /**
     * Success response with custom HTTP status.
     */
    public static <T> ApiResponse<T> success(
            String message,
            T data,
            HttpStatus status
    ) {

        return new ApiResponse<>(
                true,
                message,
                data,
                status
        );
    }


    /**
     * Error response without data.
     */
    public static <T> ApiResponse<T> error(
            String message,
            HttpStatus status
    ) {

        return new ApiResponse<>(
                false,
                message,
                null,
                status
        );
    }


    /**
     * Error response with additional error data.
     */
    public static <T> ApiResponse<T> error(
            String message,
            T data,
            HttpStatus status
    ) {

        return new ApiResponse<>(
                false,
                message,
                data,
                status
        );
    }
}