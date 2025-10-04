package com.kaidev99.ecommerce.repository;

import com.kaidev99.ecommerce.dto.ProductSuggestionDTO;
import com.kaidev99.ecommerce.entity.Product;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByIsFeaturedTrueOrderByCreatedAtDesc(Pageable pageable);

    List<Product> findByOrderByCreatedAtDesc(Pageable pageable);

    // Lấy các sản phẩm cùng category, trừ sản phẩm hiện tại
    List<Product> findTop4ByCategoryIdAndIdNot(Long categoryId, Long productId);

    @Query("SELECT new com.kaidev99.ecommerce.dto.ProductSuggestionDTO(p.id, p.name, p.price) " +
            "FROM Product p WHERE lower(p.name) LIKE lower(concat('%', :keyword, '%'))")
    List<ProductSuggestionDTO> findSuggestions(@Param("keyword") String keyword, Pageable pageable);
}