package com.kaidev99.ecommerce.config;

import com.kaidev99.ecommerce.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        String[] whiteList = {
                "/api/v1/auth/register",
                "/api/v1/auth/login",
                "/api/v1/auth/refresh",
                "/api/v1/auth/register",
                "/api/v1/cart/**"
        };


        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whiteList).permitAll()
                        // Cho phép các request GET để xem sản phẩm và danh mục
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**", "/api/v1/categories/**").permitAll()

                        // --- ĐÂY LÀ PHẦN THÊM MỚI QUAN TRỌNG ---
                        // Yêu cầu vai trò USER cho các API giỏ hàng và đơn hàng của người dùng
//                        .requestMatchers("/api/v1/cart/**").hasRole("USER")
                        .requestMatchers("/api/v1/orders/**").hasRole("USER")

                        // Yêu cầu vai trò ADMIN cho các API quản trị
                        // Mặc dù đã có @PreAuthorize, thêm ở đây là một lớp bảo vệ nữa
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // Bất kỳ request nào khác chưa được định nghĩa ở trên đều phải được xác thực
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}