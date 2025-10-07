package com.kaidev99.ecommerce.controller;

import com.kaidev99.ecommerce.dto.ChangePasswordDTO;
import com.kaidev99.ecommerce.dto.UserProfileDTO;
import com.kaidev99.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.kaidev99.ecommerce.entity.User;
import com.kaidev99.ecommerce.payload.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // --- API CHO NGƯỜI DÙNG ĐÃ ĐĂNG NHẬP ---

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        ApiResponse<User> response = ApiResponse.success(currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> updateMyProfile(
            @Valid @RequestBody UserProfileDTO profileDTO,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        User updatedUser = userService.updateMyProfile(currentUser.getId(), profileDTO);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Profile updated successfully", updatedUser));
    }

    @PatchMapping("/me/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> changeMyPassword(
            @Valid @RequestBody ChangePasswordDTO passwordDTO,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        try {
            userService.changeMyPassword(currentUser, passwordDTO);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // --- API CHO ADMIN ---

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String roleString = payload.get("role");
        User updatedUser = userService.updateUserRole(id, roleString);
        return ResponseEntity.ok(ApiResponse.success(updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(org.springframework.http.HttpStatus.OK, "User deleted successfully"));
    }
}
