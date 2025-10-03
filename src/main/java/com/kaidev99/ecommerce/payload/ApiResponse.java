package com.kaidev99.ecommerce.payload;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String message;
    private String error;
    private T data;

    public ApiResponse(HttpStatus status, String message, T data, String error) {
        this.status = status.value();
        this.message = message;
        this.data = data;
        this.error = error;
    }

    // --- Static helper methods for convenience ---

    // Response thành công với dữ liệu và message
    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status, message, data, null);
    }

    // Response thành công chỉ với message
    public static <T> ApiResponse<T> success(HttpStatus status, String message) {
        return new ApiResponse<>(status, message, null, null);
    }

    // Response thành công với dữ liệu (dùng message mặc định)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK, "Success", data, null);
    }

    // Response lỗi
    public static <T> ApiResponse<T> error(HttpStatus status, String message, String error) {
        return new ApiResponse<>(status, message, null, error);
    }

    // Response lỗi chỉ với message
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status, message, null, message);
    }
}
