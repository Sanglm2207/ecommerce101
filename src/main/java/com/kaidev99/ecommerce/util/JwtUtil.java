package com.kaidev99.ecommerce.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @Value("${app.jwt.expiration-ms}")
    private long EXPIRATION_TIME;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long REFRESH_EXPIRATION_TIME;

    /**
     * Trích xuất username (subject) từ token.
     * 
     * @param token Chuỗi JWT
     * @return Username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Trích xuất địa chỉ IP từ claim "ipAddress" của token.
     * 
     * @param token Chuỗi JWT
     * @return Địa chỉ IP
     */
    public String extractIpAddress(String token) {
        return extractClaim(token, claims -> claims.get("ipAddress", String.class));
    }

    /**
     * Trích xuất một claim cụ thể từ token bằng một hàm resolver.
     * 
     * @param token          Chuỗi JWT
     * @param claimsResolver Hàm để lấy claim mong muốn
     * @return Giá trị của claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Tạo Access Token mới cho người dùng tại một địa chỉ IP cụ thể.
     * 
     * @param userDetails Thông tin người dùng
     * @param ipAddress   Địa chỉ IP của client
     * @return Chuỗi Access Token
     */
    public String generateToken(UserDetails userDetails, String ipAddress) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("ipAddress", ipAddress);
        return buildToken(claims, userDetails, EXPIRATION_TIME);
    }

    /**
     * Tạo Refresh Token mới cho người dùng tại một địa chỉ IP cụ thể.
     * 
     * @param userDetails Thông tin người dùng
     * @param ipAddress   Địa chỉ IP của client
     * @return Chuỗi Refresh Token
     */
    public String generateRefreshToken(UserDetails userDetails, String ipAddress) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("ipAddress", ipAddress);
        return buildToken(claims, userDetails, REFRESH_EXPIRATION_TIME);
    }

    /**
     * Kiểm tra xem token có hợp lệ không.
     * Một token hợp lệ khi: username khớp, chưa hết hạn, VÀ địa chỉ IP khớp.
     * 
     * @param token            Chuỗi JWT
     * @param userDetails      Thông tin người dùng để so sánh
     * @param currentIpAddress Địa chỉ IP hiện tại của request
     * @return true nếu hợp lệ, false nếu không
     */
    public boolean isTokenValid(String token, UserDetails userDetails, String currentIpAddress) {
        final String username = extractUsername(token);
        final String tokenIp = extractIpAddress(token);
        boolean isIpMatch = currentIpAddress != null && currentIpAddress.equals(tokenIp);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) && isIpMatch;
    }

    /**
     * Phương thức private để xây dựng token (cả access và refresh).
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}