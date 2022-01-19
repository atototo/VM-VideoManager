package com.lab.vm.service;

import com.lab.vm.model.domain.Authority;
import com.lab.vm.model.domain.User;
import com.lab.vm.model.dto.RegisterDto;
import com.lab.vm.repository.AuthorityRepository;
import com.lab.vm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName : com.lab.vm.service
 * fileName : UserServiceTest
 * author : isbn8
 * date : 2022-01-19
 * description :
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-19              isbn8             최초 생성
 */
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @BeforeEach
    @DisplayName("권한 부여")
    void makeRules(){
        Authority authority = new Authority();
        authority.setName("ROLE_ADMIN");

        Authority authority2 = new Authority();
        authority2.setName("ROLE_USER");

        authorityRepository.save(authority);
        authorityRepository.save(authority2);

    }



    @Test
    @DisplayName("회원가입_정상확인")
//   @Rollback(value = false)   // 이 옵션을 두면 트랜잭션이 롤백을 하지 않아 눈으로 확인 가능하다
    void join() throws Exception {
        //given
        var user = RegisterDto.builder()
                .username("lee")
                .email("yelee@yelee.com")
                .password("1234")
                .phone("01011111111")
                .build();

        //when
        Optional<User> savedUser = userService.userJoin(user);
        BCryptPasswordEncoder b = new BCryptPasswordEncoder();

        var userDto = RegisterDto.builder()
                .id(savedUser.orElseThrow().getId())
                .username("lee")
                .email("yelee@yelee.com")
                .password("1234")
                .passwordConfirm(null)
                .phone("01011111111")
                .build();


       RegisterDto registerDto = userService.findUserInfoByName("lee");


        System.out.println(" [ findUserInfoByName ]  결과 UserDto : " + registerDto.toString());

        //then
        assertEquals(userDto.getId(), registerDto.getId());
//        assertEquals(userDto.getEmail(), regDto.orElseThrow().getEmail());
//        assertEquals(userDto.getPhone(), regDto.orElseThrow().getPhone());
        assertTrue(b.matches(userDto.getPassword(), registerDto.getPassword()));   //비빌번호 동일 여부 논리값으로 반환

    }



}