package com.kaidev99.ecommerce.controller;

import com.kaidev99.ecommerce.dto.AuthRequestDTO;
import com.kaidev99.ecommerce.entity.User;
import com.kaidev99.ecommerce.repository.UserRepository;
import com.kaidev99.ecommerce.util.JwtUtil;
import com.kaidev99.ecommerce.util.RequestUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequestDTO request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already taken!");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    };

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequestDTO requestDTO, HttpServletRequest request,
            HttpServletResponse response) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(requestDTO.username(), requestDTO.password()));

        final String ipAddress = RequestUtil.getClientIpAddress(request);
        final UserDetails userDetails = userRepository.findByUsername(requestDTO.username()).orElseThrow();

        final String accessToken = jwtUtil.generateToken(userDetails, ipAddress);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails, ipAddress);

        // Tạo cookie cho Access Token
        Cookie accessTokenCookie = new Cookie("access_token", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // Chỉ gửi qua HTTPS
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(15 * 60); // 15 phút

        // Tạo cookie cho Refresh Token
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("Login successful");
    };

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(
            @CookieValue(name = "refresh_token") String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is missing");
        }

        try {
            final String currentIpAddress = RequestUtil.getClientIpAddress(request);
            final String username = jwtUtil.extractUsername(refreshToken);
            final UserDetails userDetails = userRepository.findByUsername(username).orElseThrow();

            if (jwtUtil.isTokenValid(refreshToken, userDetails, currentIpAddress)) {

                // Cấp lại Access Token MỚI với IP hiện tại
                final String newAccessToken = jwtUtil.generateToken(userDetails, currentIpAddress);

                Cookie accessTokenCookie = new Cookie("access_token", newAccessToken);
                accessTokenCookie.setHttpOnly(true);
                accessTokenCookie.setSecure(true); // false for localhost
                accessTokenCookie.setPath("/");
                accessTokenCookie.setMaxAge(15 * 60);

                response.addCookie(accessTokenCookie);

                return ResponseEntity.ok("Token refreshed successfully");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    };

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("access_token", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");

        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("Logout successful");
    }

}