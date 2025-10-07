package com.kaidev99.ecommerce.service.Impl;

import com.kaidev99.ecommerce.dto.ChangePasswordDTO;
import com.kaidev99.ecommerce.dto.UserProfileDTO;
import com.kaidev99.ecommerce.entity.Role;
import com.kaidev99.ecommerce.entity.User;
import com.kaidev99.ecommerce.exception.ResourceNotFoundException;
import com.kaidev99.ecommerce.repository.UserRepository;
import com.kaidev99.ecommerce.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    @Transactional
    public User updateMyProfile(Long userId, UserProfileDTO profileDTO) {
        User user = getUserById(userId);

        user.setEmail(profileDTO.email());
        user.setFullName(profileDTO.fullName());
        user.setPhone(profileDTO.phone());
        user.setAddress(profileDTO.address());
        user.setAvatarUrl(profileDTO.avatarUrl());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void changeMyPassword(User user, ChangePasswordDTO passwordDTO) {
        // Kiểm tra mật khẩu hiện tại có đúng không
        if (!passwordEncoder.matches(passwordDTO.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password.");
        }

        // Kiểm tra mật khẩu mới và xác nhận có khớp không
        if (!passwordDTO.newPassword().equals(passwordDTO.confirmationPassword())) {
            throw new IllegalArgumentException("New password and confirmation password do not match.");
        }

        // Mã hóa và cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(passwordDTO.newPassword()));
        userRepository.save(user);
    }
}