package com.lab.vm.repository;

import com.lab.vm.model.dto.RegisterDto;
import com.lab.vm.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class UserRepositoryTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;



    void makeUser() {
        var user  = RegisterDto.builder()
//            .activated(true)
                .username("user")
                .password("user")
                .passwordConfirm("user")
                .phone("01011113333")
                .email("user@user.com")
                .build();

        userService.registerUser(user);
    }

    void makeAdmin() {
        var user  = RegisterDto.builder()
//            .activated(true)
                .username("admin")
                .password("admin")
                .passwordConfirm("admin")
                .phone("01011113333")
                .email("user@user.com")
                .build();

        userService.registerUser(user);
    }

    @Test
    @DisplayName("유저명으로 권한 정보 까지 조회")
    void findOneWithAuthoritiesByUsername(){
        // admin 권한 2가지 : ROLE_ADMIN, ROL_USER

        //given
        makeAdmin();

        // when
        var user = userRepository.findOneWithAuthoritiesByUsername("admin");
        //then
        assertAll(
                () -> assertTrue(user.isPresent()),
                () -> assertEquals(2, user.get().getAuthorities().size())
        );

    }

    @Test
    @DisplayName("유저명으로 단건 조회")
    void findAllByUserName() {
        //given
        makeUser();

        var user = userRepository.findAllByUsername("user");


        //then
        assertAll(
                () -> assertTrue(user.isPresent()),
                () -> assertEquals(1, user.get().getAuthorities().size()),
                () -> assertEquals("user", user.get().getUsername()),
                () -> assertEquals("01011113333", user.get().getPhone()),
                () -> assertEquals("user@user.com", user.get().getEmail())

        );
   }

    @Test
    @DisplayName("User Entity 가 아닌 RegisterDto 로 필요항목만 조회")
    void findUserInfoByName(){
        //given
        makeAdmin();

        //when
        var registerDto = userRepository.findUserInfoByName("admin");

        //then
        assertAll(
                () -> assertNotNull(registerDto.getId()),
                () -> assertEquals("admin", registerDto.getUsername()),
                () -> assertNotNull(registerDto.getPassword()),
                () -> assertNotNull(registerDto.getPhone()),
                () -> assertNotNull(registerDto.getEmail())

        );
    }


}