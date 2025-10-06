package com.kaidev99.ecommerce.controller;

import com.kaidev99.ecommerce.dto.AuthRequestDTO;
import com.kaidev99.ecommerce.payload.ApiResponse;
import com.kaidev99.ecommerce.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody AuthRequestDTO request) {
        try {
            authService.register(request);
            return new ResponseEntity<>(ApiResponse.success(HttpStatus.CREATED, "User registered successfully!"), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(HttpStatus.CONFLICT, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody AuthRequestDTO requestDTO, HttpServletRequest request,
                                                   HttpServletResponse httpResponse) {
        // Bọc trong try-catch để xử lý lỗi sai mật khẩu từ AuthenticationManager
        try {
            authService.login(requestDTO, request, httpResponse);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Login successful"));
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(HttpStatus.UNAUTHORIZED, "Invalid username or password"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(
            @CookieValue(name = "refresh_token") String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            authService.refreshToken(refreshToken, request, response);
            return ResponseEntity.ok("Token refreshed successfully");
        } catch (Exception e) {
            // Trả về lỗi chung chung để bảo mật
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse httpResponse) {
        authService.logout(httpResponse);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Logout successful"));
    }

}