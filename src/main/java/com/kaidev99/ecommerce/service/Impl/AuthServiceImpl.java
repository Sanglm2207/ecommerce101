package com.kaidev99.ecommerce.service.Impl;

import com.kaidev99.ecommerce.dto.AuthRequestDTO;
import com.kaidev99.ecommerce.dto.NotificationPayload;
import com.kaidev99.ecommerce.entity.Role;
import com.kaidev99.ecommerce.entity.User;
import com.kaidev99.ecommerce.payload.ApiResponse;
import com.kaidev99.ecommerce.repository.UserRepository;
import com.kaidev99.ecommerce.service.AuthService;
import com.kaidev99.ecommerce.service.EventPublisher;
import com.kaidev99.ecommerce.util.JwtUtil;
import com.kaidev99.ecommerce.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl  implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EventPublisher eventPublisher;

    @Override
    public User register(AuthRequestDTO request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken!");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);

        // Gửi sự kiện người dùng mới
        NotificationPayload payload = NotificationPayload.builder()
                .type("NEW_USER")
                .message("Tài khoản mới vừa được đăng ký: " + savedUser.getUsername())
                .link("/admin/users/" + savedUser.getId())
                .timestamp(LocalDateTime.now())
                .build();
        eventPublisher.publishNotification("notification.admin", payload);

        return savedUser;
    }

    @Override
    public void login(AuthRequestDTO request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        final String ipAddress = RequestUtil.getClientIpAddress(httpRequest);
        final UserDetails userDetails = userRepository.findByUsername(request.username()).orElseThrow();

        final String accessToken = jwtUtil.generateToken(userDetails, ipAddress);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails, ipAddress);

        addCookie(httpResponse, "access_token", accessToken, 15 * 60);
        addCookie(httpResponse, "refresh_token", refreshToken, 7 * 24 * 60 * 60);
    }

    @Override
    public void refreshToken(String refreshToken, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token is missing");
        }

        try {
            final String currentIpAddress = RequestUtil.getClientIpAddress(httpRequest);
            final String username = jwtUtil.extractUsername(refreshToken);
            final UserDetails userDetails = userRepository.findByUsername(username).orElseThrow();

            if (jwtUtil.isTokenValid(refreshToken, userDetails, currentIpAddress)) {
                final String newAccessToken = jwtUtil.generateToken(userDetails, currentIpAddress);
                addCookie(httpResponse, "access_token", newAccessToken, 15 * 60);
            } else {
                throw new SecurityException("Invalid refresh token");
            }
        } catch (Exception e) {
            throw new SecurityException("Invalid refresh token", e);
        }
    }

    @Override
    public void logout(HttpServletResponse httpResponse) {
        // Tạo cookie rỗng với maxAge = 0 để yêu cầu trình duyệt xóa
        addCookie(httpResponse, "access_token", null, 0);
        addCookie(httpResponse, "refresh_token", null, 0);
    }

    /**
     * Hàm tiện ích private để tạo và thêm cookie vào response.
     */
    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Đặt là false để test trên localhost, đổi thành true khi deploy
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
