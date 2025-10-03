package com.kaidev99.ecommerce;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kaidev99.ecommerce.entity.Role;
import com.kaidev99.ecommerce.entity.User;
import com.kaidev99.ecommerce.repository.UserRepository;

@SpringBootApplication
public class EcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

	// Bean này sẽ được thực thi ngay sau khi ứng dụng khởi động
	@Bean
	public CommandLineRunner commandLineRunner(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			// Kiểm tra xem tài khoản admin đã tồn tại chưa
			if (userRepository.findByUsername("admin").isEmpty()) {
				System.out.println("Creating ADMIN user...");
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("123456aA@"));
				admin.setRole(Role.ADMIN);
				userRepository.save(admin);
				System.out.println("ADMIN user created!");
			}
		};
	}
}
