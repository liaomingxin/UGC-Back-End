package com.ugc.backend.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private String details;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .code(200)
            .message("success")
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> error(int code, String message, String details) {
        return ApiResponse.<T>builder()
            .code(code)
            .message(message)
            .details(details)
            .build();
    }
} 