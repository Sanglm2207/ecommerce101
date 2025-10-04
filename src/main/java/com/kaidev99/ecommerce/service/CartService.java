package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.dto.CartItemDTO;
import com.kaidev99.ecommerce.dto.UpdateCartDTO;
import java.util.List;

public interface CartService {
    void addOrUpdateItem(String username, UpdateCartDTO cartDTO);
    List<CartItemDTO> getCart(String username);
    void removeItem(String username, Long productId);
    void clearCart(String username);
    void removeMultipleItems(String username, List<Long> productIds); // THÊM PHƯƠNG THỨC MỚI

}
