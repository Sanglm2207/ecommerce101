package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.dto.CreateCouponDTO;
import com.kaidev99.ecommerce.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CouponService {

    List<Coupon> createCoupons(CreateCouponDTO createCouponDTO);

    Page<Coupon> getAllCoupons(Specification<Coupon> spec, Pageable pageable);

    Coupon toggleCouponStatus(Long id);

    Coupon getCouponById(Long id);

    Coupon validateCoupon(String code);
}