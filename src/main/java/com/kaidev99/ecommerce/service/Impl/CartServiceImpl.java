package com.kaidev99.ecommerce.service.Impl;

import com.kaidev99.ecommerce.dto.CartItemDTO;
import com.kaidev99.ecommerce.dto.UpdateCartDTO;
import com.kaidev99.ecommerce.entity.Product;
import com.kaidev99.ecommerce.exception.ResourceNotFoundException;
import com.kaidev99.ecommerce.repository.ProductRepository;
import com.kaidev99.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ProductRepository productRepository;

    private static final String CART_KEY_PREFIX = "cart:";


    @Override
    public void addOrUpdateItem(String username, UpdateCartDTO cartDTO) {
        // Kiểm tra xem sản phẩm có tồn tại không
        productRepository.findById(cartDTO.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + cartDTO.productId()));

        String cartKey = CART_KEY_PREFIX + username;
        String field = "productId:" + cartDTO.productId();
        String value = String.valueOf(cartDTO.quantity());

        // Ghi vào Redis Hash
        redisTemplate.opsForHash().put(cartKey, field, value);
    }

    @Override
    public List<CartItemDTO> getCart(String username) {
        String cartKey = CART_KEY_PREFIX + username;
        Map<Object, Object> items = redisTemplate.opsForHash().entries(cartKey);

        return items.entrySet().stream()
                .map(entry -> {
                    Long productId = Long.parseLong(((String) entry.getKey()).split(":")[1]);
                    int quantity = Integer.parseInt((String) entry.getValue());
                    Product product = productRepository.findById(productId)
                            .orElse(null); // Hoặc xử lý khác nếu sản phẩm bị xóa
                    return new CartItemDTO(product, quantity);
                })
                .filter(item -> item.product() != null) // Lọc bỏ các sản phẩm không còn tồn tại
                .collect(Collectors.toList());
    }

    @Override
    public void removeItem(String username, Long productId) {
        String cartKey = CART_KEY_PREFIX + username;
        String field = "productId:" + productId;
        redisTemplate.opsForHash().delete(cartKey, field);
    }

    @Override
    public void clearCart(String username) {
        String cartKey = CART_KEY_PREFIX + username;
        redisTemplate.delete(cartKey);
    }

    @Override
    public void removeMultipleItems(String username, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return;
        }
        String cartKey = CART_KEY_PREFIX + username;

        // Chuyển danh sách ID thành mảng các field key của Redis Hash
        Object[] fields = productIds.stream()
                .map(id -> "productId:" + id)
                .toArray();

        // Gọi lệnh xóa nhiều field một lúc
        redisTemplate.opsForHash().delete(cartKey, fields);
    }
}
