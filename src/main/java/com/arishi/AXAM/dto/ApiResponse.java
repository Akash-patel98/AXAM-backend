package com.arishi.AXAM.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Builder
public class ApiResponse<T> {

    private final boolean status;

    private final int statusCode;

    private final String message;

    private final String errorCode;

    private final T data;

    private final Instant timestamp;


    public static <T> ApiResponse<T> success(int statusCode, String message, T data) {
        return ApiResponse.<T>builder().status(true).statusCode(statusCode).message(message).data(data).timestamp(Instant.now()).build();
    }


    public static <T> ApiResponse<T> success(int statusCode, String message) {
        return ApiResponse.<T>builder().status(true).statusCode(statusCode).message(message).timestamp(Instant.now()).build();
    }


    public static <T> ApiResponse<T> error(int statusCode, String errorCode, String message) {
        return ApiResponse.<T>builder().status(false).statusCode(statusCode).errorCode(errorCode).message(message).timestamp(Instant.now()).build();
    }


}
