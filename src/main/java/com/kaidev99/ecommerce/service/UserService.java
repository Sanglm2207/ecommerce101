package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    /**
     * Lấy danh sách tất cả người dùng (dành cho Admin).
     * @param pageable Thông tin phân trang
     * @return Một trang các người dùng
     */
    Page<User> getAllUsers(Pageable pageable);

    /**
     * Lấy thông tin chi tiết một người dùng bằng ID.
     * @param id ID của người dùng
     * @return Đối tượng User
     */
    User getUserById(Long id);

    /**
     * Cập nhật vai trò của một người dùng (dành cho Admin).
     * @param id ID của người dùng cần cập nhật
     * @param roleString Tên vai trò mới ("ADMIN" hoặc "USER")
     * @return Người dùng sau khi đã được cập nhật
     */
    User updateUserRole(Long id, String roleString);

    /**
     * Xóa một người dùng (dành cho Admin).
     * @param id ID của người dùng cần xóa
     */
    void deleteUser(Long id);
}