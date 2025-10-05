package com.kaidev99.ecommerce.service.Impl;

import com.kaidev99.ecommerce.entity.Role;
import com.kaidev99.ecommerce.entity.User;
import com.kaidev99.ecommerce.exception.ResourceNotFoundException;
import com.kaidev99.ecommerce.repository.UserRepository;
import com.kaidev99.ecommerce.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public User updateUserRole(Long id, String roleString) {
        // 1. Tìm người dùng
        User user = getUserById(id);

        // 2. Chuyển đổi String thành Enum Role một cách an toàn
        try {
            Role newRole = Role.valueOf(roleString.toUpperCase());
            user.setRole(newRole);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleString + ". Valid roles are USER, ADMIN.");
        }

        // 3. Không cho phép tự xóa quyền admin của chính mình (nếu logic cần)
        // Hoặc không cho phép xóa quyền của tài khoản admin gốc
        if (user.getUsername().equals("admin")) {
            throw new IllegalArgumentException("Cannot change role of the root admin account.");
        }

        // 4. Lưu lại
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);

        // Thêm các quy tắc bảo vệ nếu cần
        if (user.getUsername().equals("admin")) {
            throw new IllegalArgumentException("Cannot delete the root admin account.");
        }

        userRepository.deleteById(id);
    }
}