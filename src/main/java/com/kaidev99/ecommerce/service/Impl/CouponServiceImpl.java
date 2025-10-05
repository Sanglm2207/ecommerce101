package com.kaidev99.ecommerce.service.Impl;

import com.kaidev99.ecommerce.dto.CreateCouponDTO;
import com.kaidev99.ecommerce.entity.Coupon;
import com.kaidev99.ecommerce.exception.ResourceNotFoundException;
import com.kaidev99.ecommerce.repository.CouponRepository;
import com.kaidev99.ecommerce.service.CouponService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public List<Coupon> createCoupons(CreateCouponDTO dto) {
        List<Coupon> createdCoupons = new ArrayList<>();
        // Sử dụng một Set để kiểm tra tính duy nhất trong batch hiện tại
        Set<String> generatedCodesInBatch = new HashSet<>();

        for (int i = 0; i < dto.quantity(); i++) {
            Coupon coupon = new Coupon();
            String generatedCode;

            // Lặp lại cho đến khi tạo được một mã hoàn toàn mới
            do {
                generatedCode = generateRandomCode(dto.codePrefix());
            } while (generatedCodesInBatch.contains(generatedCode) || couponRepository.findByCode(generatedCode).isPresent());

            generatedCodesInBatch.add(generatedCode); // Thêm mã mới vào Set

            coupon.setCode(generatedCode);
            coupon.setDiscountType(dto.discountType());
            coupon.setDiscountValue(dto.discountValue());
            coupon.setMaxUsage(dto.maxUsage());
            coupon.setExpiryDate(dto.expiryDate());
            coupon.setActive(true);
            coupon.setUsageCount(0);

            createdCoupons.add(coupon);
        }

        return couponRepository.saveAll(createdCoupons);
    }

    @Override
    public Page<Coupon> getAllCoupons(Specification<Coupon> spec, Pageable pageable) {
        return couponRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public Coupon toggleCouponStatus(Long id) {
        Coupon coupon = getCouponById(id);
        coupon.setActive(!coupon.isActive()); // Đảo ngược trạng thái
        return couponRepository.save(coupon);
    }

    @Override
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
    }

    @Override
    public Coupon validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá không hợp lệ."));

        // Kiểm tra các điều kiện
        if (!coupon.isActive()) {
            throw new IllegalArgumentException("Mã giảm giá đã bị vô hiệu hóa.");
        }
        if (coupon.getUsageCount() >= coupon.getMaxUsage()) {
            throw new IllegalArgumentException("Mã giảm giá đã hết lượt sử dụng.");
        }
        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Mã giảm giá đã hết hạn.");
        }

        // TODO: kiểm tra giá trị đơn hàng tối thiểu

        return coupon;
    }

    /**
     * Tạo một mã ngẫu nhiên và đảm bảo nó là duy nhất trong DB.
     * @param prefix Tiền tố cho mã (có thể là null).
     * @return Một mã coupon duy nhất.
     */
    private String generateRandomCode(String prefix) {
        // Độ dài hậu tố ngẫu nhiên
        int randomLength = 8;
        if (prefix != null && !prefix.isEmpty()) {
            randomLength = Math.max(4, 12 - prefix.length());
        }

        StringBuilder sb = new StringBuilder(randomLength);
        for (int i = 0; i < randomLength; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return (prefix != null ? prefix.toUpperCase() : "") + sb.toString();
    }
}
