package com.lab.vm;

import com.lab.vm.model.domain.Authority;
import com.lab.vm.model.dto.RegisterDto;
import com.lab.vm.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VmWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(VmWebApplication.class, args);
	}


	/**
	 * 스프링 부트는 run() 이라는 콜백 메소드를 가진 CommendLineRunner라는 인터페이스 제공
	 * run() 메소드는 Spring application context의 초기화가 완료된(모든 Baan이 초기화된) 후에 실행되므로
	 * 이 안에 원하는 로직을 작성하면 된다.
	 */
	@Bean
	public CommandLineRunner run(UserService userService) {
		return (String[] args) -> {


			var admin = RegisterDto.builder()
//            .activated(true)
					.username("admin")
					.password("admin")
					.phone("01011112222")
					.email("admin@admin.com")
					.build();

			userService.registerUser(admin);

			var user  = RegisterDto.builder()
//            .activated(true)
					.username("user")
					.password("user")
					.phone("01011113333")
					.email("user@user.com")
					.build();

			userService.registerUser(user);


		};
	}
}
