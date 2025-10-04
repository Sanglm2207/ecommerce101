package com.kaidev99.ecommerce.controller;

import com.kaidev99.ecommerce.dto.CartItemDTO;
import com.kaidev99.ecommerce.dto.UpdateCartDTO;
import com.kaidev99.ecommerce.payload.ApiResponse;
import com.kaidev99.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // Lấy username của người dùng đang đăng nhập
    private String getCurrentUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemDTO>>> getCart(Authentication authentication) {
        String username = getCurrentUsername(authentication);
        List<CartItemDTO> cart = cartService.getCart(username);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addItemToCart(@Valid @RequestBody UpdateCartDTO cartDTO, Authentication authentication) {
        String username = getCurrentUsername(authentication);
        cartService.addOrUpdateItem(username, cartDTO);
        return new ResponseEntity<>(ApiResponse.success(HttpStatus.OK, "Item added/updated in cart"), HttpStatus.OK);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeItemFromCart(@PathVariable Long productId, Authentication authentication) {
        String username = getCurrentUsername(authentication);
        cartService.removeItem(username, productId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Item removed from cart"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {
        String username = getCurrentUsername(authentication);
        cartService.clearCart(username);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Cart cleared"));
    }

    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse<Void>> removeMultipleItemsFromCart(
            @RequestBody Map<String, List<Long>> payload,
            Authentication authentication) {

        List<Long> productIds = payload.get("productIds");
        if (productIds == null || productIds.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.error(HttpStatus.BAD_REQUEST, "Product IDs are required."), HttpStatus.BAD_REQUEST);
        }

        String username = getCurrentUsername(authentication);
        cartService.removeMultipleItems(username, productIds);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Selected items removed from cart"));
    }
}
