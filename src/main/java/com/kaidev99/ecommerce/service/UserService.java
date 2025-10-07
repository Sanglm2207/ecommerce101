package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.dto.ChangePasswordDTO;
import com.kaidev99.ecommerce.dto.UserProfileDTO;
import com.kaidev99.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<User> getAllUsers(Pageable pageable);

    User getUserById(Long id);

    User updateUserRole(Long id, String roleString);

    void deleteUser(Long id);

    User updateMyProfile(Long userId, UserProfileDTO profileDTO);

    void changeMyPassword(User user, ChangePasswordDTO passwordDTO);
}