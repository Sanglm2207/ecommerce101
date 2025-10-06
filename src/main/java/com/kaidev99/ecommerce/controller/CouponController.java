package com.kaidev99.ecommerce.controller;

import com.kaidev99.ecommerce.dto.CreateCouponDTO;
import com.kaidev99.ecommerce.entity.Coupon;
import com.kaidev99.ecommerce.payload.ApiResponse;
import com.kaidev99.ecommerce.service.CouponService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Coupon>>> createCoupons(@Valid @RequestBody CreateCouponDTO createCouponDTO) {
        List<Coupon> newCoupons = couponService.createCoupons(createCouponDTO);
        return new ResponseEntity<>(ApiResponse.success(HttpStatus.CREATED, "Coupons created successfully", newCoupons), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<Coupon>>> getAllCoupons(
            @Filter(entityClass = Coupon.class) Specification<Coupon> spec,
            Pageable pageable) {

        // Tạo một Specification trống để đảm bảo không bao giờ là null
        Specification<Coupon> finalSpec = spec != null ? spec : Specification.where((Specification<Coupon>) null);

        // Chỉ AND với spec từ filter nếu nó không null (thư viện sẽ trả về null nếu filter rỗng)
        if (spec != null) {
            finalSpec = finalSpec.and(spec);
        }

        Page<Coupon> couponPage = couponService.getAllCoupons(finalSpec, pageable);
        return ResponseEntity.ok(ApiResponse.success(couponPage));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Coupon>> getCouponById(@PathVariable Long id) {
        Coupon coupon = couponService.getCouponById(id);
        return ResponseEntity.ok(ApiResponse.success(coupon));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Coupon>> toggleCouponStatus(@PathVariable Long id) {
        Coupon updatedCoupon = couponService.toggleCouponStatus(id);
        String message = updatedCoupon.isActive() ? "Coupon activated successfully" : "Coupon deactivated successfully";
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, message, updatedCoupon));
    }

    @PostMapping("/validate")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Coupon>> validateCoupon(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        if (code == null || code.trim().isEmpty()) {
            return new ResponseEntity<>(ApiResponse.error(HttpStatus.BAD_REQUEST, "Coupon code is required"), HttpStatus.BAD_REQUEST);
        }

        // Gọi service để kiểm tra
        Coupon validCoupon = couponService.validateCoupon(code);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Coupon is valid", validCoupon));
    }
}