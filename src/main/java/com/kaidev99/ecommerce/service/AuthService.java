package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.dto.AuthRequestDTO;
import com.kaidev99.ecommerce.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    /**
     * Đăng ký một người dùng mới.
     * @param request DTO chứa username và password.
     * @return Đối tượng User đã được tạo.
     */
    User register(AuthRequestDTO request);

    /**
     * Xử lý logic đăng nhập, xác thực và tạo cookie.
     * @param request DTO chứa username và password.
     * @param httpRequest Đối tượng request để lấy IP.
     * @param httpResponse Đối tượng response để set cookie.
     */
    void login(AuthRequestDTO request, HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    /**
     * Xử lý logic làm mới access token.
     * @param refreshToken Chuỗi refresh token từ cookie.
     * @param httpRequest Đối tượng request để lấy IP.
     * @param httpResponse Đối tượng response để set cookie access token mới.
     */
    void refreshToken(String refreshToken, HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    /**
     * Xử lý logic đăng xuất, xóa cookie.
     * @param httpResponse Đối tượng response để xóa cookie.
     */
    void logout(HttpServletResponse httpResponse);
}
